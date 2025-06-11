package com.fastcampus.payment.service;


import com.fastcampus.payment.common.exception.BadRequestException;
import com.fastcampus.payment.common.exception.error.PaymentErrorCode;
import com.fastcampus.payment.common.util.TokenHandler;
import com.fastcampus.payment.dto.PaymentExecutionResponse;
import com.fastcampus.payment.dto.PaymentExecutionRequest;
import com.fastcampus.payment.entity.*;
import com.fastcampus.payment.repository.*;
import com.fastcampus.paymentmethod.entity.CardInfo;
import com.fastcampus.paymentmethod.entity.PaymentMethod;
import com.fastcampus.paymentmethod.entity.PaymentMethodType;
import com.fastcampus.paymentmethod.entity.UseYn;
import com.fastcampus.paymentmethod.repository.CardInfoRepository;
import com.fastcampus.paymentmethod.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * 결제 실행 로직을 담당하는 서비스 구현체
 * -거래 token과 카드 token을 받아 승인 여부 판단
 * -거래 상태를 COMPLETED or FAILED 로 변경하고, DB/Redis에 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentExecutionServiceImpl implements PaymentExecutionService {

    // 🔥 수정: @Autowired 제거하고 final로 주입 (생성자 주입)
    private final TransactionRepository transactionRepository; //JPA 저장소
    private final TransactionRepositoryRedis redisTransactionRepository; //Redis 저장소
    private final CardInfoRepository cardInfoRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private final PaymentRepository paymentRepository;

    @Autowired
    private final TokenHandler tokenHandler;
    /**
     * 결제 요청을 실행하고 거래 상태를 갱신합니다.
     */
    @Override
    @Transactional
    public PaymentExecutionResponse execute(PaymentExecutionRequest request) {
        log.info("결제 실행 시작 - transactionToken: {}",request.getPaymentToken());
        //입력 값 검증
        validateRequest(request);

        //1. 거래 조회 (Redis -> DB Fallback)
        Payment payment = findPayment(request.getPaymentToken());
        validatePaymentStatus(payment);

        // 3. 카드 & 결제수단 검증
        CardInfo cardInfo = validateAndGetCardInfo(request.getCardToken());
        PaymentMethod paymentMethod = validatePaymentMethod(request.getPaymentMethodType(), request.getUserId(), cardInfo);

        // 4. 결제 방식에 따른 승인 처리
        boolean approvalResult = processPaymentByMethod(request, paymentMethod);

        //상태 결정 및 업데이트(수정)
        PaymentStatus newStatus = approvalResult ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

        // 데이터 업데이트
        Transaction tx = new Transaction(payment);
        updatePaymentData(payment, tx, paymentMethod, newStatus, request);
        updateTransactionData(payment, tx, paymentMethod, cardInfo);

        //4. DB에 저장
        savePaymentData(payment, tx);

        // 5. Redis 상태 갱신 (주석 처리된 부분 활성화)
        try {
//            redisTransactionRepository.update(tx);
        } catch (Exception e) {
            log.warn("Redis 업데이트 실패, DB는 정상 저장됨", e);
        }


        log.info("결제 실행 완료- paymentId: {}, 상태: {}", payment.getId(), tx.getStatus());

        //6. 결과 반환
        return new PaymentExecutionResponse(payment);
    }

    /**
     * 카드 정보 검증 및 조회
     */
    private CardInfo validateAndGetCardInfo(String cardToken) {
        return cardInfoRepository.findByToken(cardToken)
                .orElseThrow(() -> new BadRequestException(PaymentErrorCode.CARD_NOT_FOUND));
    }

    /**
     * 결제 수단 검증
     */
    private PaymentMethod validatePaymentMethod(String methodType, Long userId, CardInfo cardInfo) {
        // String을 Enum으로 변환하여 검증
        PaymentMethodType enumType;
        try {
            enumType = PaymentMethodType.fromString(methodType);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        }

        List<PaymentMethod> methodList = paymentMethodRepository.findByUserIdAndMethodType(userId, enumType);
        if(methodList.isEmpty()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_METHOD_NOT_FOUND);
        }
        List<PaymentMethod> yList = methodList.stream()
                .filter(ele -> UseYn.Y.equals(ele.getUseYn()))
                .collect(Collectors.toList()); 
        if(yList.isEmpty()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_METHOD_NOT_FOUND);
        }

        PaymentMethod method = methodList.get(0);   // TODO - 한 userId 와 한 method type 으로 조회 했는데 paymentMethod 결과가 여러 개일 경우 어떻게 처리할지? (예 - 신용 카드만 여러 개)
        if(!method.getPaymentMethodId().equals(cardInfo.getPaymentMethod().getPaymentMethodId())) {

                // 요청한 userId 로 찾은 결과와 cardToken 으로 가져온 결과가 서로 다름
            throw new BadRequestException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        }

        return method;
    }

    /**
     * 🔥 수정: PaymentExecutionRequest용 검증 메서드
     */
    private void validateRequest(PaymentExecutionRequest request) {
        if(request.getPaymentToken() == null || request.getPaymentToken().trim().isEmpty()){
            throw new BadRequestException(PaymentErrorCode.PAYMENT_EXECUTION_NULL_VALUE);
        }
        if (request.getCardToken() == null || request.getCardToken().trim().isEmpty()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_EXECUTION_NULL_VALUE);
        }
        if (request.getPaymentMethodType() == null || request.getPaymentMethodType().trim().isEmpty()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_EXECUTION_NULL_VALUE);
        }
    }

    /**
     * 주어진 거래 토큰으로 Redis에서 거래를 조회하고, 없을 경우 데이터베이스에서 조회합니다.
     *
     * @param token 조회할 결제 정보의 토큰
     * @return 조회된 거래 엔티티
     * @throws RuntimeException 거래를 찾을 수 없는 경우 발생
     */
    private Payment findPayment(String token) {
        Long paymentId = tokenHandler.decodeQrToken(token);

        return paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new BadRequestException(PaymentErrorCode.PAYMENT_NOT_FOUND));
    }

    /**
     * 거래의 상태와 만료 여부를 검증하여 유효하지 않을 경우 예외를 발생시킵니다.
     */
    private void validatePaymentStatus(Payment payment) {
        if (payment.getStatus().isFinal()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);
        }
    }

    /**
     * 결제 방식에 따른 승인 처리
     */
    private boolean processPaymentByMethod(PaymentExecutionRequest request, PaymentMethod paymentMethod) {
        PaymentMethodType methodType = paymentMethod.getType();

        return switch (methodType) {
            case CARD -> CardApproval(request.getCardToken(), methodType);
            case BANK_TRANSFER -> BankTransferApproval(request.getCardToken(), methodType);
            case MOBILE_PAY -> MobilePayApproval(request.getCardToken(), methodType);
            case CRYPTO -> CryptoApproval(request.getCardToken(), methodType);
            case PAYPAL -> PaypalApproval(request.getCardToken(), methodType);
            case APPLE_PAY, GOOGLE_PAY -> WalletPayApproval(request.getCardToken(), methodType);
        };
    }

    /**
     * 카드 승인 과정을 시뮬레이션합니다.
     */
    private boolean CardApproval(String cardToken, PaymentMethodType methodType) {
        try {
            Thread.sleep(methodType.getProcessingTimeMs());
            return new Random().nextInt(100) < methodType.getSuccessRate();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("카드 승인 시뮬레이션 중 인터럽트 발생", e);
            return false;
        }
    }

    /**
     * 계좌이체 승인 시뮬레이션
     */
    private boolean BankTransferApproval(String accountToken, PaymentMethodType methodType) {
        try {
            Thread.sleep(methodType.getProcessingTimeMs());
            // 계좌 잔액 확인 시뮬레이션
            // TODO: 실제 잔액 조회 서비스 구현 후 활성화
            // 현재는 기본 성공률로 처리
            return new Random().nextInt(100) < methodType.getSuccessRate();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 모바일페이 승인 시뮬레이션
     */
    private boolean MobilePayApproval(String mobilePayToken, PaymentMethodType methodType) {
        try {
            Thread.sleep(methodType.getProcessingTimeMs());
            return new Random().nextInt(100) < methodType.getSuccessRate();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 암호화폐 승인 시뮬레이션
     */
    private boolean CryptoApproval(String cryptoWalletToken, PaymentMethodType methodType) {
        try {
            Thread.sleep(methodType.getProcessingTimeMs()); // 블록체인 확인 시간
            // 네트워크 혼잡 시뮬레이션
            if (new Random().nextInt(100) < 10) {
                log.warn("블록체인 네트워크 혼잡으로 인한 지연");
                Thread.sleep(1000); // 추가 지연
            }
            return new Random().nextInt(100) < methodType.getSuccessRate();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * PayPal 승인 시뮬레이션
     */
    private boolean PaypalApproval(String paypalToken, PaymentMethodType methodType) {
        try {
            Thread.sleep(methodType.getProcessingTimeMs());
            return new Random().nextInt(100) < methodType.getSuccessRate();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 지갑형 결제 (Apple Pay, Google Pay) 승인 시뮬레이션
     */
    private boolean WalletPayApproval(String walletToken, PaymentMethodType methodType) {
        try {
            Thread.sleep(methodType.getProcessingTimeMs());
            return new Random().nextInt(100) < methodType.getSuccessRate();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }


    private void updatePaymentData(Payment payment, Transaction transaction, PaymentMethod paymentMethod, PaymentStatus paymentStatus, PaymentExecutionRequest request) {
        payment.setStatus(paymentStatus);
        payment.changeLastTransaction(transaction);
        payment.setUser(paymentMethod.getUser());
    }

    private void updateTransactionData(Payment payment, Transaction transaction, PaymentMethod paymentMethod, CardInfo cardInfo) {
        transaction.setPaymentMethod(paymentMethod);
        transaction.setCardToken(cardInfo.getToken());
        transaction.changePayment(payment);
        transaction.setAmount(payment.getTotalAmount());    // TODO - 결제할 금액은 총액 : payment 안에 들고 있던 totalAmount
    }

    private void savePaymentData(Payment payment, Transaction transaction) {
        paymentRepository.save(payment);
        transactionRepository.save(transaction);
    }

}

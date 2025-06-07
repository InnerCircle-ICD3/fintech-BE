package com.fastcampus.payment.service;

import com.fastcampus.payment.common.exception.BadRequestException;
import com.fastcampus.payment.common.exception.error.PaymentErrorCode;
import com.fastcampus.payment.dto.PaymentExecutionRequest;
import com.fastcampus.payment.dto.PaymentProgressResponse;
import com.fastcampus.payment.entity.CardInfo;
import com.fastcampus.payment.entity.PaymentMethod;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.entity.TransactionStatus;
import com.fastcampus.payment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

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

    /**
     * 결제 요청을 실행하고 거래 상태를 갱신합니다.
     */
    @Override
    @Transactional
    public PaymentProgressResponse execute(PaymentExecutionRequest request) {
        log.info("결제 실행 시작 - transactionToken: {}", request.getTransactionToken());

        // 입력 값 검증
        validateRequest(request);

        // 1. 거래 조회 (Redis -> DB Fallback)
        Transaction tx = findTransaction(request.getTransactionToken());
        validateTransactionStatus(tx);

        // 2. 카드 & 결제수단 검증
        CardInfo cardInfo = validateAndGetCardInfo(request.getCardToken());
        PaymentMethod paymentMethod = validatePaymentMethod(request.getPaymentMethodType());

        // 3. 카드 승인 여부 판단(시뮬레이션)
        boolean approvalResult = simulateCardApproval(request.getCardToken());

        // 상태 결정 및 업데이트
        TransactionStatus newStatus = approvalResult ? TransactionStatus.COMPLETED : TransactionStatus.FAILED;
        tx.setStatus(newStatus);
        tx.setCardToken(request.getCardToken());

        // 4. DB에 저장
        transactionRepository.save(tx);

        // 5. Redis 상태 갱신 (주석 처리된 부분 활성화)
        try {
            redisTransactionRepository.update(tx);
        } catch (Exception e) {
            log.warn("Redis 업데이트 실패, DB는 정상 저장됨", e);
        }

        log.info("결제 실행 완료 - transactionToken: {}, 상태: {}", tx.getTransactionToken(), tx.getStatus());

        // 6. 결과 반환
        return PaymentProgressResponse.builder()
                .transactionToken(tx.getTransactionToken())
                .status(newStatus) // 🔥 수정: 그냥 Enum을 전달, DTO의 getStatus()에서 String으로 변환
                .amount(tx.getAmount())
                .merchantId(Long.toString(tx.getMerchantId()))
                .merchantOrderId(tx.getMerchantOrderId())
                .createdAt(tx.getCreatedAt())
                .cardInfo(cardInfo)
                .paymentMethod(paymentMethod)
                .approvalResult(approvalResult)
                .build();
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
    private PaymentMethod validatePaymentMethod(String methodType) {
        PaymentMethod method = paymentMethodRepository.findByType(methodType)
                .orElseThrow(() -> new BadRequestException(PaymentErrorCode.INVALID_PAYMENT_METHOD));

        if (!method.getIsActive()) {
            throw new BadRequestException(PaymentErrorCode.INACTIVE_PAYMENT_METHOD);
        }

        return method;
    }

    /**
     * 🔥 수정: PaymentExecutionRequest용 검증 메서드
     */
    private void validateRequest(PaymentExecutionRequest request) {
        if (request.getTransactionToken() == null || request.getTransactionToken().trim().isEmpty()) {
            throw new IllegalArgumentException("transactionToken은 필수값입니다.");
        }
        if (request.getCardToken() == null || request.getCardToken().trim().isEmpty()) {
            throw new IllegalArgumentException("cardToken은 필수값입니다.");
        }
        if (request.getPaymentMethodType() == null || request.getPaymentMethodType().trim().isEmpty()) {
            throw new IllegalArgumentException("paymentMethodType은 필수값입니다.");
        }
    }

    /**
     * 주어진 거래 토큰으로 Redis에서 거래를 조회하고, 없을 경우 데이터베이스에서 조회합니다.
     */
    private Transaction findTransaction(String transactionToken) {
        try {
            return redisTransactionRepository.findByToken(transactionToken)
                    .orElseGet(() -> transactionRepository.findByTransactionToken(transactionToken)
                            .orElseThrow(() -> new BadRequestException(PaymentErrorCode.PAYMENT_NOT_FOUND)));
        } catch (Exception e) {
            // Redis 오류 시 DB에서 직접 조회
            log.warn("Redis 조회 실패, DB에서 직접 조회합니다.", e);
            return transactionRepository.findByTransactionToken(transactionToken)
                    .orElseThrow(() -> new BadRequestException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        }
    }

    /**
     * 거래의 상태와 만료 여부를 검증하여 유효하지 않을 경우 예외를 발생시킵니다.
     */
    private void validateTransactionStatus(Transaction tx) {
        if (tx.getStatus().isFinal()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);
        }
    }

    /**
     * 카드 승인 과정을 90% 확률로 성공하는 방식으로 시뮬레이션합니다.
     */
    private boolean simulateCardApproval(String cardToken) {
        try {
            // 실제 카드사 API 호출 시뮬레이션
            Thread.sleep(100);
            // 90% 확률로 승인 성공
            return new Random().nextInt(100) < 90;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("카드 승인 시뮬레이션 중 인터럽트 발생", e);
            return false;
        }
    }
}
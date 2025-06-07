package com.fastcampus.payment.service;


import com.fastcampus.payment.common.exception.BadRequestException;
import com.fastcampus.payment.common.exception.error.PaymentErrorCode;
import com.fastcampus.payment.dto.PaymentExecutionRequest;
import com.fastcampus.payment.dto.PaymentProgressRequest;
import com.fastcampus.payment.dto.PaymentProgressResponse;
import com.fastcampus.payment.entity.CardInfo;
import com.fastcampus.payment.entity.PaymentMethod;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.entity.TransactionStatus;
import com.fastcampus.payment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final TransactionRepository transactionRepository; //jap 저장소


    private final TransactionRepositoryRedis redisTransactionRepository; //Redis 저장소

    @Autowired
    private final CardInfoRepository cardInfoRepository;

    @Autowired
    private final PaymentMethodRepository paymentMethodRepository;


    /**
     * 결제 요청을 실행하고 거래 상태를 갱신합니다.
     * <p>
     * 결제 요청의 유효성을 검증한 후, 거래를 조회하여 이미 완료된 거래인지 확인합니다.
     * 카드 승인 시뮬레이션을 통해 승인 여부에 따라 거래 상태를 COMPLETED 또는 FAILED로 변경하고,
     * 변경된 거래 정보를 데이터베이스와 Redis에 반영합니다.
     * 최종적으로 갱신된 거래 정보를 포함한 응답을 반환합니다.
     *
     * @param request 결제 실행에 필요한 거래 토큰과 카드 토큰을 포함한 요청 객체
     * @return 처리 결과를 담은 PaymentProgressResponse 객체
     * @throws IllegalArgumentException 요청 값이 유효하지 않을 경우
     * @throws RuntimeException         거래를 찾을 수 없을 경우
     * @throws IllegalStateException    이미 완료된 거래이거나 처리할 수 없는 상태일 경우
     */
    @Override
    @Transactional
    public PaymentProgressResponse execute(PaymentExecutionRequest request) {
        log.info("결제 실행 시작 - transactionToken: {}",request.getTransactionToken());
        //입력 값 검증
        validateRequest(request);

        //1. 거래 조회 (Redis -> DB Fallback)
        Transaction tx = findTransaction(request.getTransactionToken());
        validateTransactionStatus(tx);

        // 2. 카드 & 결제수단 검증
        CardInfo cardInfo = validateAndGetCardInfo(request.getCardToken());
        PaymentMethod paymentMethod = validatePaymentMethod(request.getPaymentMethodType());

        //3. 카드 승인 여부 판단(시뮬레이션)
        boolean approvalResult = simulateCardApproval(request.getCardToken());

        //상태 결정 및 업데이트(수정)
        TransactionStatus newStatus = approvalResult ? TransactionStatus.COMPLETED : TransactionStatus.FAILED;
        tx.setStatus(newStatus);
        tx.setCardToken(request.getCardToken());

        //4. DB에 저장
        transactionRepository.save(tx);

        //5. Redis 상태 갱신
        redisTransactionRepository.update(tx);

        log.info("결제 실행 완료- transactionToken: {}, 상태: {}", tx.getTransactionToken(), tx.getStatus());

        //6. 결과 반환
        return PaymentProgressResponse.builder()
                .transactionToken(tx.getTransactionToken())
                .status(tx.getStatus())
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
     * 요청 검증 (기존 로직 개선)
     */
    private void validateRequest(PaymentProgressRequest request) {
        request.nullCheckRequiredParam();

        // 추가 검증 로직
        if (request.getCardToken() == null || request.getCardToken().trim().isEmpty()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_PROGRESS_NULL_VALUE);
        }

        if (request.getPaymentMethodType() == null || request.getPaymentMethodType().trim().isEmpty()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_PROGRESS_NULL_VALUE);
        }
    }

    /**
     * 결제 진행 요청의 필수 필드인 transactionToken과 cardToken이 null이거나 비어 있는지 검증합니다.
     *
     * @param request 결제 진행 요청 객체
     * @throws IllegalArgumentException 필수 값이 누락된 경우 발생합니다.
     */
    private void validateRequest(PaymentExecutionRequest request) {
        if(request.getTransactionToken() == null || request.getTransactionToken().trim().isEmpty()){
            throw new IllegalArgumentException("transactionToken은 필수값입니다.");
        }
        if(request.getCardToken() == null || request.getCardToken().trim().isEmpty()){
            throw new IllegalArgumentException("cardToken은 필수값입니다.");
        }
    }
    /**
     * 주어진 거래 토큰으로 Redis에서 거래를 조회하고, 없을 경우 데이터베이스에서 조회합니다.
     *
     * @param transactionToken 조회할 거래의 토큰
     * @return 조회된 거래 엔티티
     * @throws RuntimeException 거래를 찾을 수 없는 경우 발생
     */
    private Transaction findTransaction(String transactionToken) {
        return redisTransactionRepository.findByToken(transactionToken)
                .orElseGet(() -> transactionRepository.findByTransactionToken(transactionToken)
                        .orElseThrow(() -> new BadRequestException(PaymentErrorCode.PAYMENT_NOT_FOUND)));
    }

    /**
     * 거래의 상태와 만료 여부를 검증하여 유효하지 않을 경우 예외를 발생시킵니다.
     *
     * 거래 상태가 설정되지 않았거나, 이미 완료된 상태이거나, 만료 시간이 지났을 경우 {@code IllegalStateException}을 던집니다.
     *
     * @param tx 검증할 거래 객체
     * @throws IllegalStateException 거래 상태가 유효하지 않거나 만료된 경우
     */
    private void validateTransactionStatus(Transaction tx) {
        if (tx.getStatus().isFinal()) {
            throw new BadRequestException(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);
        }
    }

    /**
     * 카드 승인 과정을 90% 확률로 성공하는 방식으로 시뮬레이션합니다.
     *
     * 카드사와의 실제 연동 없이 승인 성공 또는 실패를 무작위로 결정합니다. 
     * 승인 과정 중 인터럽트가 발생하면 실패로 간주합니다.
     *
     * @param cardToken 카드 식별자
     * @return 승인 성공 시 true, 실패 시 false
     */
    private boolean simulateCardApproval(String cardToken) {
       try {
           //실제 카드사 API 호출 시뮬레이션
           Thread.sleep(100);
           //90% 확률로 승인 성공
              return new Random().nextInt(100) < 90;
       }catch (InterruptedException e){
           Thread.currentThread().interrupt();
           log.error("카드 승인 시뮬레이션 중 인터럽트 발생", e);
           return false;
       }
    }


    /**
     * 카드 승인 시뮬레이션
     *
     */

    private boolean simulateCardApproval(CardInfo cardInfo) {
        // 실제로는 PG사 API 호출
        // 여기서는 시뮬레이션
        if (cardInfo.getCardCompany().equals("BLOCKED")) {
            throw new BadRequestException(PaymentErrorCode.CARD_APPROVAL_FAILED);
        }
        return true;
    }
}

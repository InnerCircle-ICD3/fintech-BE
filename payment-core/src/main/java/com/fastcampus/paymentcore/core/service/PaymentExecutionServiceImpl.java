package com.fastcampus.paymentcore.core.service;


import com.fastcampus.paymentcore.core.dto.PaymentProgressRequest;
import com.fastcampus.paymentcore.core.dto.PaymentProgressResponse;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.redis.RedisTransactionRepository;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import com.fastcampus.paymentinfra.type.TransactionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final RedisTransactionRepository redisTransactionRepository; //Redis 저장소

    /**
     * 결제 실행 메서드
     * @param request 결제 실행 요청 (transactionToken + cardToken)
     * @return 실행 결과 (transactionToken + 상태)
     */
    @Override
    @Transactional
    public PaymentProgressResponse execute(PaymentProgressRequest   request) {
        log.info("결제 실행 시작 - transactionToken: {}",request.getTransactionToken());
        //입력 값 검증
        validateRequest(request);

        //1. 거래 조회 (Redis -> DB Fallback)
        Transaction tx = redisTransactionRepository.findByToken(request.getTransactionToken())
                .orElseGet(() -> transactionRepository.findByTransactionToken(request.getTransactionToken())
                        .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다.")));

        //2. 거래 상태 검증 추가
        if(tx.getStatus().isFinal()){
            throw new IllegalStateException("이미 완료된 거래입니다.:" + tx.getStatus());
        }

        //3. 카드 승인 여부 판단(시뮬레이션)
        boolean approved = simulateCardApproval(request.getCardToken());

        //상태 결정 및 업데이트(수정)
        TransactionStatus newStatus = approved ? TransactionStatus.COMPLETED : TransactionStatus.FAILED;
        tx.setStatus(newStatus);
        tx.setCardToken(request.getCardToken());

        //4. DB에 저장
        transactionRepository.save(tx);

        //5. Redis 상태 갱신
        redisTransactionRepository.update(tx);

        log.info("결제 실행 완료- transactionToken: {}, 상태: {}", tx.getTransactionToken(), tx.getStatus());

        //6. 결과 반환
       return new PaymentProgressResponse(tx);

    }

    private void validateRequest(PaymentProgressRequest request) {
        if(request.getTransactionToken() == null || request.getTransactionToken().trim().isEmpty()){
            throw new IllegalArgumentException("transactionToken은 필수값입니다.");
        }
        if(request.getCardToken() == null || request.getCardToken().trim().isEmpty()){
            throw new IllegalArgumentException("cardToken은 필수값입니다.");
        }
    }
    private Transaction findTransaction(String transactionToken) {
        return redisTransactionRepository.findByToken(transactionToken)
                .orElseGet(() -> {
                    log.info("Redis에서 거래 조회 실패, DB에서 조회 - transactionToken: {}", transactionToken);
                    return transactionRepository.findByTransactionToken(transactionToken)
                            .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다: " + transactionToken));
                });
    }

    private void validateTransactionStatus(Transaction tx) {
        if (tx.getStatus() == null) {
            throw new IllegalStateException("거래 상태가 설정되지 않았습니다.");
        }
        if(tx.getStatus().isFinal()){
            throw new IllegalStateException("이미 완료된 거래입니다. 현재 상태: " + tx.getStatus());

        }
        //만료 시간 검증 추가
        if(tx.getExpireAt() != null && tx.getExpireAt().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("거래가 만료되었습니다.");
        }
    }

    /**
     * 카드 승인 시뮬레이션 로직
     * 실제 카드사 연동이 아니라 랜덤으로 성공 여부 판단
     * @param cardToken 카드 식별자
     * @return 승인 여부 (true: 승인, false: 실패)
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
}

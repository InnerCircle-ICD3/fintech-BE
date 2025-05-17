package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.dto.PaymentProgressRequest;
import com.fastcampus.paymentcore.core.dto.PaymentProgressResponse;
import com.fastcampus.paymentcore.core.dummy.TransactionEntityDummy;
import com.fastcampus.paymentcore.core.dummy.TransactionRepositoryDummy;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.redis.RedisTransactionRepository;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;


/**
 * 결제 실행 로직을 담당하는 서비스 구현체
 * -거래 token과 카드 token을 받아 승인 여부 판단
 * -거래 상태를 COMPLETED or FAILED 로 변경하고, DB/Redis에 저장
 */
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
    public PaymentProgressResponse execute(PaymentProgressRequest   request) {
        //1. 거래 조회 (Redis -> DB Fallback)
        Transaction tx = redisTransactionRepository.findByToken(request.getTransactionToken())
                .orElseGet(() -> transactionRepository.findByTransactionToken(request.getTransactionToken())
                        .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다.")));

        //2. 카드 승인 여부 판단(시뮬레이션)
        boolean approved = simulateCardApproval(request.getCardToken());

        //3. 상태 결정 및 업데이트
        String status = approved ? "COMPLETED" : "FAILED";
        tx.setStatus(status);
        tx.setCardToken(request.getCardToken());

        //4. DB에 저장
        transactionRepository.save(tx);

        //5. Redis 상태 갱신
        redisTransactionRepository.update(tx);

        //6. 결과 반환
        return new PaymentProgressResponse(request.getTransactionToken(), status);

    }
    /**
     * 카드 승인 시뮬레이션 로직
     * 실제 카드사 연동이 아니라 랜덤으로 성공 여부 판단
     * @param cardToken 카드 식별자
     * @return 승인 여부 (true: 승인, false: 실패)
     */
    private boolean simulateCardApproval(String cardToken) {
        //90%확률로 성공
        return new Random().nextInt(100) < 90;
    }
}

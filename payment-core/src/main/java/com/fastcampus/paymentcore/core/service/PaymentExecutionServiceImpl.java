package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.PaymentProgressRequest;
import com.fastcampus.paymentcore.core.dto.PaymentProgressResponse;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.entity.TransactionStatus;
import com.fastcampus.paymentinfra.redis.RedisTransactionRepository;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentExecutionServiceImpl implements PaymentExecutionService {

    private final TransactionRepository transactionRepository;
    private final RedisTransactionRepository redisTransactionRepository;
    private final TokenHandler tokenHandler;

    @Override
    public PaymentProgressResponse execute(PaymentProgressRequest request) {
        // 1. JWT에서 거래 ID 및 원본 토큰 추출
        Claims claims = tokenHandler.decodeQrTokenToClaims(request.getTransactionToken());
        Long transactionId = claims.get("transactionId", Long.class);
        String originalToken = request.getTransactionToken();

        // 2. 거래 조회 (Redis → DB fallback)
        Optional<Transaction> redisTx = redisTransactionRepository.findByToken(originalToken);
        Transaction tx = redisTx.orElseGet(() ->
                transactionRepository.findById(transactionId)
                        .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다."))
        );

        // 3. 카드 승인 여부 판단
        boolean approved = simulateCardApproval(request.getCardToken());
        TransactionStatus status = approved ? TransactionStatus.COMPLETED : TransactionStatus.FAILED;

        // 4. 상태 갱신
        tx.setStatus(status);
        tx.setCardToken(request.getCardToken());

        // 5. 저장 (DB + Redis)
        transactionRepository.save(tx);
        redisTransactionRepository.save(tx, claims.getExpiration().getTime() / 1000 - System.currentTimeMillis() / 1000);

        // 6. 응답 반환
        return new PaymentProgressResponse(originalToken, status);
    }

    private boolean simulateCardApproval(String cardToken) {
        return new Random().nextInt(100) < 90;
    }
}

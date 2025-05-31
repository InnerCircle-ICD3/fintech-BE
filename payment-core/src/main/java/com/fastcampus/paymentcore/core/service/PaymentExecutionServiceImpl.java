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
        // 250531 - 세현: 란영 님. PaymentProgressResponse 클래스는 제가 PaymentProgressResponse.progressPayment() 에서 쓰려고 만든 response class 입니다
        // 제 생각에는 PaymentExecutionService 에서 쓰실 dto class 를 새로 만드시는 게 더 좋을 것 같아요!
        // 일단 제 부분 수정하면서 컴파일 에러 때문에 어쩔 수 없이 같이 수정했습니다. 영향도 있는지 보시고 추후에 다시 수정해 주세요!
        Transaction transaction =  new Transaction();
        transaction.setTransactionToken(originalToken);
        transaction.setStatus(status);
        return new PaymentProgressResponse(transaction);
    }

    private boolean simulateCardApproval(String cardToken) {
        return new Random().nextInt(100) < 90;
    }
}

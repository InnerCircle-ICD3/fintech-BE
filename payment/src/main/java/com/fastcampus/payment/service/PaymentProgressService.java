package com.fastcampus.payment.service;

import com.fastcampus.payment.common.util.TokenHandler;
import com.fastcampus.payment.common.idem.Idempotent;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProgressService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentProgressService.class);

    private final TransactionRepository transactionRepository;
    private final TokenHandler tokenHandler;

    @Idempotent
    public Transaction progressPayment(String transactionToken) {
        // QR 토큰에서 거래 ID 디코딩
        Long transactionId = tokenHandler.decodeQrToken(transactionToken);

        // 거래 조회
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다"));

        return transaction;
    }
}

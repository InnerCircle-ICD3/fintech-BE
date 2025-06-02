package com.fastcampus.paymentcore.core.service;

import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.common.exception.exception.BadRequestException;
import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.PaymentProgressRequest;
import com.fastcampus.paymentcore.core.dto.PaymentProgressResponse;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
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
    public PaymentProgressResponse progressPayment(PaymentProgressRequest request) {
        // QR 토큰에서 거래 ID 디코딩
        Long transactionId = tokenHandler.decodeQrToken(request.getTransactionToken());

        // 거래 조회
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BadRequestException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        return new PaymentProgressResponse(transaction);
    }
}

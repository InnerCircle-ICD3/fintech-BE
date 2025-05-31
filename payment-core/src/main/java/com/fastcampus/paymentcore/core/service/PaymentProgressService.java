package com.fastcampus.paymentcore.core.service;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;
import com.fastcampus.paymentcore.core.dto.ProgressTransactionRequest;
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
    public PaymentProgressDto progressPayment(ProgressTransactionRequest request) {
        String token = request.getTransactionToken();
        Long transactionId = tokenHandler.decodeQrToken(token);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new HttpException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        return new PaymentProgressDto(transaction);
    }
}

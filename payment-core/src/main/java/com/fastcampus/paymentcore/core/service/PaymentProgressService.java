package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProgressService {

    private final TransactionRepository transactionRepository;
    private final TokenHandler tokenHandler;

    @Idempotent
    public PaymentProgressDto progressPayment(String token) {
        Long transactionId = (long) tokenHandler.decodeQrToken(token);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return new PaymentProgressDto(transaction);
    }
}

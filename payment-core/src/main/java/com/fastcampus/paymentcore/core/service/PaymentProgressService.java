package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;
import com.fastcampus.paymentcore.core.dummy.TransactionEntityDummy;
import com.fastcampus.paymentcore.core.dummy.TransactionRepositoryDummy;
import org.springframework.stereotype.Service;

@Service
public class PaymentProgressService {

    @Idempotent
    public PaymentProgressDto progressPayment(String token) {
        //
        TokenHandler tokenHandler = new TokenHandler();
        int transactionId = tokenHandler.decodeQrToken(token);
        TransactionRepositoryDummy transactionRepository = new TransactionRepositoryDummy();
        TransactionEntityDummy transactionEntity = transactionRepository.find(transactionId);
        PaymentProgressDto paymentProgressDto = new PaymentProgressDto(transactionEntity);
        return paymentProgressDto;
    }
}

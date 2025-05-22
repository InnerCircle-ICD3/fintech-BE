package com.fastcampus.paymentcore.core.service;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;
import com.fastcampus.paymentcore.core.dummy.TransactionEntityDummy;
import com.fastcampus.paymentcore.core.dummy.TransactionRepositoryDummy;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentProgressService {

    Logger logger = LoggerFactory.getLogger(PaymentProgressService.class);

    @Autowired
    TransactionRepository transactionRepository;
    @Idempotent
    public PaymentProgressDto progressPayment(String token) {
        //
        Optional<Transaction> transactionOpt = transactionRepository.findByTransactionToken(token);
        if(transactionOpt.isEmpty()) {
            throw new HttpException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
        Transaction transaction = transactionOpt.get();
        PaymentProgressDto paymentProgressDto = new PaymentProgressDto(transaction);
        return paymentProgressDto;
    }
}

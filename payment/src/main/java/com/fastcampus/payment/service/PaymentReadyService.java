package com.fastcampus.payment.service;

import com.fastcampus.payment.common.util.CommonUtil;
import com.fastcampus.payment.common.util.SystemParameterUtil;
import com.fastcampus.payment.common.util.TokenHandler;
import com.fastcampus.payment.common.idem.Idempotent;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentReadyService {

    private final TransactionRepository transactionRepository;
    private final TokenHandler tokenHandler;
    private final SystemParameterUtil systemParameterUtil;
    private final CommonUtil commonUtil;

    @Idempotent
    public Transaction readyPayment(Transaction transaction) {
        nullCheckReadyPayment(transaction);
        checkPaymentStatus(transaction);
        saveTransaction(transaction);
        inputTransactionValues(transaction);
        return transaction;
    }

    private void nullCheckReadyPayment(Transaction transaction) {
        transaction.nullCheckRequiredParam();
    }

    /**
     * 해당 payment 가 이미 결제 완료된 상태인지 status 를 체크
     * @param request - 결제 준비 요청 객체
     * @throws RuntimeException - 결제 상태가 중복된 주문이면 예외 발생 PaymentErrorCode.DUPLICATE_ORDER
     */
    private void checkPaymentStatus(Transaction request) {
        // transactionToken 자체가 없다면 최초 요청이므로 그냥 통과
        if (request.getTransactionToken() == null || request.getTransactionToken().isBlank()) return;

        // 해당 payment 가 이미 결제 완료된 상태인지 status 를 체크
        Optional<Transaction> transactionOps = transactionRepository.findByTransactionToken(request.getTransactionToken());
        transactionOps.ifPresent((transaction)->{
            transaction.checkStatusAlreadyDone();
        });
    }


    private Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    private Transaction inputTransactionValues(Transaction transaction) {
        String transactionToken = tokenHandler.generateTokenWithTransactionId(transaction.getTransactionId());
        transaction.setTransactionToken(transactionToken);
        transaction.setExpireAt(commonUtil.generateExpiresAt());
        return transaction;
    }


}

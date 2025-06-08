package com.fastcampus.payment.service;

import com.fastcampus.payment.common.util.CommonUtil;
import com.fastcampus.payment.common.util.TokenHandler;
import com.fastcampus.payment.common.idem.Idempotent;
import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.entity.PaymentStatus;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.repository.PaymentRepository;
import com.fastcampus.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentReadyService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final TokenHandler tokenHandler;
    private final CommonUtil commonUtil;

    @Idempotent
    @Transactional
    public Payment readyPayment(Payment payment) {
        nullCheckReadyPayment(payment);
        savePayment(payment);
        Transaction transaction = makeTransaction(payment);
        saveTransaction(transaction);
        inputPaymentValues(payment, transaction);
        return payment;
    }

    private void nullCheckReadyPayment(Payment payment) {
        payment.nullCheckRequiredParam();
    }



    private Payment savePayment(Payment payment) {
        payment.setStatus(PaymentStatus.READY);
        return paymentRepository.save(payment);
    }

    private Transaction saveTransaction(Transaction transaction) {
        LocalDateTime exp = commonUtil.generateExpiresAt();
        transaction.setExpireAt(commonUtil.generateExpiresAt());
        return transactionRepository.save(transaction);
    }


    private Transaction makeTransaction(Payment payment) {
        Transaction transaction = new Transaction(payment);
        return transaction;
    }

    private void inputPaymentValues(Payment payment, Transaction transaction) {
        String token = tokenHandler.generateTokenWithPayment(payment);
        payment.setToken(token);
    }


}

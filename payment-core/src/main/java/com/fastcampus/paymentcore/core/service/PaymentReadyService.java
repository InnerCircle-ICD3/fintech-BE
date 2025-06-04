package com.fastcampus.paymentcore.core.service;

import com.fastcampus.common.exception.exception.BadRequestException;
import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.CommonUtil;
import com.fastcampus.paymentcore.core.common.util.SystemParameterUtil;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.PaymentReadyRequest;
import com.fastcampus.paymentcore.core.dto.PaymentReadyResponse;
import com.fastcampus.paymentcore.core.entity.Transaction;
import com.fastcampus.paymentcore.core.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentReadyService {

    private final TransactionRepository transactionRepository;
    private final TokenHandler tokenHandler;
    private final SystemParameterUtil systemParameterUtil;
    private final CommonUtil commonUtil;

    @Idempotent
    public PaymentReadyResponse readyPayment(PaymentReadyRequest request) {
        nullCheckReadyPayment(request);
        checkPaymentStatus(request);
        Transaction transaction = saveTransaction(request);
        String token = tokenHandler.generateTokenWithTransactionId(transaction.getTransactionId());
        LocalDateTime expiresAt = transaction.getExpireAt();
        return new PaymentReadyResponse(token, expiresAt);
    }

    private void nullCheckReadyPayment(PaymentReadyRequest request) {
        request.nullCheckRequiredParam();
    }

    /**
     * 해당 payment 가 이미 결제 완료된 상태인지 status 를 체크
     * @param request - 결제 준비 요청 객체
     * @throws BadRequestException - 결제 상태가 중복된 주문이면 예외 발생 PaymentErrorCode.DUPLICATE_ORDER
     */
    private void checkPaymentStatus(PaymentReadyRequest request) {
        // transactionToken 자체가 없다면 최초 요청이므로 그냥 통과
        if (request.getTransactionToken() == null || request.getTransactionToken().isBlank()) return;

        // 해당 payment 가 이미 결제 완료된 상태인지 status 를 체크
        Optional<Transaction> transactionOps = transactionRepository.findByTransactionToken(request.getTransactionToken());
        transactionOps.ifPresent((transaction)->{
            transaction.checkStatusAlreadyDone();
        });
    }


    private Transaction saveTransaction(PaymentReadyRequest request) {
        Transaction transaction = request.convertToTransaction();
        transaction.setExpireAt(commonUtil.generateExpiresAt());    // 얘만 여기 두는 게 맞아? PaymentReadyRequest.convertToTransaction() 안에 같이 두고 싶은데;; 거기 넣으면 commonUtil bean 주입을 못 받음
        return transactionRepository.save(transaction);
    }


}

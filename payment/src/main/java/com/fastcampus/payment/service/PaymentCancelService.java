package com.fastcampus.payment.service;

import com.fastcampus.payment.common.exception.base.HttpException;
import com.fastcampus.payment.common.exception.error.PaymentErrorCode;
import com.fastcampus.payment.common.idem.Idempotent;
import com.fastcampus.payment.common.util.TokenHandler;
import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.entity.PaymentStatus;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.repository.PaymentRepository;
import com.fastcampus.payment.repository.TransactionRepository;
import com.fastcampus.paymentmethod.entity.CardInfo;
import com.fastcampus.paymentmethod.entity.PaymentMethod;
import com.fastcampus.paymentmethod.entity.UseYn;
import com.fastcampus.paymentmethod.repository.CardInfoRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaymentCancelService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentCancelService.class);

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final CardInfoRepository cardInfoRepository;
    private final TokenHandler tokenHandler;

    @Idempotent
    @Transactional
    public Payment cancelPayment(String token) {
        // payment 조회
        Payment payment = extractPaymentByToken(token);
        // 결제 정보가 취소 가능한 status인지 확인
        try {
            // try catch 문을 여기다 두는 게 맞나...?
            checkPaymentStatusCancel(payment);
        } catch(HttpException e) {
            if(PaymentErrorCode.PAYMENT_ALREADY_CANCELED.equals(e.getErrorCode())) {
                return payment;
            } else {
                throw e;
            }
        }
        // 결제 수단이 취소 가능한지 확인
        checkPaymentMethodCancellable(payment);
        // 취소 실행
        doCancelPayment(payment);
        // transaction 생성
        Transaction newTransaction = makeCancelTransaction(payment);
        // payment update
        Payment newPayment = updatePaymentData(payment, newTransaction);

        return newPayment;
    }

    private Payment extractPaymentByToken(String token) {
        // 토큰으로 payment 조회
        Optional<Payment> paymentOpt = Optional.empty();
        try {
            // QR 토큰에서 거래 ID 디코딩
            Long paymentId = tokenHandler.decodeQrToken(token);
            // 거래 조회
            paymentOpt = paymentRepository.findById(paymentId);
        } catch (ExpiredJwtException e) {
            // tokenHandler.decodeQrToken(); 중에 token 만료 exception 발생 시
            throw new HttpException(PaymentErrorCode.PAYMENT_EXPIRED);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        // payment 있으면 반환하고 없으면 NOT FOUND 예외 발생
        Payment payment = paymentOpt.orElseThrow(() -> new HttpException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        return payment;
    }

    private void checkPaymentStatusCancel(Payment payment) {
        // 멱등한 api 를 위해 이미 취소된 거래라면 같은 결과 던지기
        if(PaymentStatus.CANCELED.equals(payment.getStatus())) {
            throw new HttpException(PaymentErrorCode.PAYMENT_ALREADY_CANCELED);
        }
        // 결제가 취소 가능한 결제인지 결제 상태 확인
        if(!(PaymentStatus.COMPLETED.equals(payment.getStatus()))) {
            throw new HttpException(PaymentErrorCode.PAYMENT_ILLEGAL_STATE);
        }

    }

    private void checkPaymentMethodCancellable(Payment payment) {
        //
        Transaction transaction = payment.getLastTransaction();
        PaymentMethod paymentMethod = transaction.getPaymentMethod();
        // 결제 수단이 카드라면 카드 정보가 취소 가능한 상태인지 먼저 확인
        CardInfo cardInfo = cardInfoRepository.findByPaymentMethodId(paymentMethod.getId())
                .orElseThrow(()->{throw new HttpException(PaymentErrorCode.CARD_NOT_FOUND);});
        checkCardCancellable(cardInfo, transaction);
        // TODO - 카드 외 결제 수단의 결제 취소 확인은 추후 확장
    }

    private void checkCardCancellable(CardInfo cardInfo, Transaction transaction) {
        // 카드 정보와 결제 정보를 카드사에 보내고 현재 취소 가능한 상태인지 확인
        if(UseYn.N.equals(transaction.getPaymentMethod().getUseYn())) {
            throw new HttpException(PaymentErrorCode.INACTIVE_PAYMENT_METHOD);
        }
        boolean isCancellable = true; // 실제 카드사 없으니 일단 무조건 true return 하도록 해둠
        if(!isCancellable) {
            throw new HttpException(PaymentErrorCode.CARD_INVALID_STATUS);
        }
    }

    private void doCancelPayment(Payment payment) {
        //
        Transaction transaction = payment.getLastTransaction();
        PaymentMethod paymentMethod = transaction.getPaymentMethod();
        CardInfo cardInfo = cardInfoRepository.findByPaymentMethodId(paymentMethod.getId())
                .orElseThrow(()->{throw new HttpException(PaymentErrorCode.CARD_NOT_FOUND);});
        //
        doCancelCard(cardInfo);
        // TODO - 카드 외 결제 수단의 결제 취소는 추후 확장
    }

    private void doCancelCard(CardInfo cardInfo) {
        // TODO - 카드사에 취소 요청
    }

    private Transaction makeCancelTransaction(Payment payment) {
        Transaction oldTransaction = payment.getLastTransaction();
        Transaction newTransaction = new Transaction(payment);
        newTransaction.setStatus(PaymentStatus.CANCELED);
        newTransaction.setAmount(oldTransaction.getAmount());
        newTransaction.setCardToken(oldTransaction.getCardToken());
        transactionRepository.save(newTransaction);
        return newTransaction;
    }

    private Payment updatePaymentData(Payment payment, Transaction transaction) {
        payment.changeLastTransaction(transaction);
        payment.setStatus(transaction.getStatus());
        return paymentRepository.save(payment);
    }
}

package com.fastcampus.paymentcore.core.service;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.SystemParameterUtil;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.PaymentReadyRequestDto;
import com.fastcampus.paymentcore.core.dto.ResponsePaymentReady;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.entity.TransactionStatus;
import com.fastcampus.paymentinfra.repository.MerchantRepository;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class PaymentReadyService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private SystemParameterUtil systemParameterUtil;

    @Value("${lifetime.qr}")
    private String ttlQr;

    @Value("${time.zoneId}")
    private String zoneId;

    @Idempotent
    public ResponsePaymentReady readyPayment(PaymentReadyRequestDto request) {
        nullCheckReadyPayment(request);
        checkMerchantExists(request.merchantId());
        checkPaymentStatus(request);
        Long transactionId = saveTransaction(request);
        String token = tokenHandler.generateTokenWithTransactionId(transactionId);
        LocalDateTime expiresAt = generateExpiresAt();
        return new ResponsePaymentReady(token, expiresAt);
    }

    private void nullCheckReadyPayment(PaymentReadyRequestDto request) {
        if (request.merchantId() == null ||
                request.merchantOrderId() == null ||
                request.amount() == null) {
            throw new HttpException(PaymentErrorCode.INVALID_PAYMENT_REQUEST);
        }
    }

    private void checkMerchantExists(Long merchantId) {
        boolean exists = merchantRepository.existsById(merchantId);
        if (!exists) {
            throw new HttpException(PaymentErrorCode.INVALID_MERCHANT_ID);
        }
    }

    private void checkPaymentStatus(PaymentReadyRequestDto request) {
        Optional<Transaction> existing = transactionRepository.findByMerchantIdAndMerchantOrderId(
                request.merchantId(),
                request.merchantOrderId()
        );
        if (existing.isPresent() && !TransactionStatus.REQUESTED.equals(existing.get().getStatus())) {
            throw new HttpException(PaymentErrorCode.DUPLICATE_ORDER);
        }
    }

    private LocalDateTime generateExpiresAt() {
        Clock clock = Clock.system(ZoneId.of(zoneId));
        return LocalDateTime.now(clock).plusSeconds(Integer.parseInt(ttlQr));
    }

    private Long saveTransaction(PaymentReadyRequestDto request) {
        Transaction transaction = new Transaction(
                request.merchantId(),
                request.merchantOrderId(),
                request.amount(),
                TransactionStatus.REQUESTED,
                null,                // transactionToken: 아직 없음
                null,                // cardToken: 아직 없음
                LocalDateTime.now(Clock.system(ZoneId.of(zoneId))),
                generateExpiresAt()
        );
        Transaction saved = transactionRepository.save(transaction);
        return saved.getTransactionId();
    }
}

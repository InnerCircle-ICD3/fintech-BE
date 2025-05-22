package com.fastcampus.paymentcore.core.service;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.SystemParameterUtil;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.ResponsePaymentReady;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
public class PaymentReadyService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentReadyService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private SystemParameterUtil systemParameterUtil;

    @Value("${lifetime.qr}")
    private String ttlQr;

    @Value("${time.zoneId}")
    private String zoneId;

    @Idempotent
    public ResponsePaymentReady readyPayment(Map<String, Object> paramMap) {
        if (!nullCheckReadyPayment(paramMap)) {
            throw new HttpException(PaymentErrorCode.INVALID_PAYMENT_REQUEST);
        }

        if (checkPaymentStatus(paramMap)) {
            // TODO: 결제 완료 상태 처리 필요 시 구현
        }

        String token = tokenHandler.generateTokenPaymentReady();
        saveTransaction(paramMap, token);
        LocalDateTime expiresAt = generateExpiresAt();

        return new ResponsePaymentReady(token, expiresAt);
    }

    private boolean nullCheckReadyPayment(Map<String, Object> paramMap) {
        return paramMap.get("merchantId") != null &&
                paramMap.get("merchantOrderId") != null &&
                paramMap.get("amount") != null;
    }

    private boolean checkPaymentStatus(Map<String, Object> paramMap) {
        // TODO - 결제 상태 확인 로직 구현 필요
        return false;
    }

    private LocalDateTime generateExpiresAt() {
        Clock clock = Clock.system(ZoneId.of(zoneId));
        return LocalDateTime.now(clock).plusSeconds(Integer.parseInt(ttlQr));
    }

    private void saveTransaction(Map<String, Object> paramMap, String token) {
        Transaction transaction = new Transaction();
        transaction.setMerchantId(Long.parseLong(paramMap.get("merchantId").toString()));
        transaction.setMerchantOrderId(paramMap.get("merchantOrderId").toString());
        transaction.setAmount(Long.parseLong(paramMap.get("amount").toString()));
        transaction.setTransactionToken(token);
        transaction.setStatus("READY");
        transaction.setCreatedAt(LocalDateTime.now(Clock.system(ZoneId.of(zoneId))));

        transactionRepository.save(transaction);
    }
}
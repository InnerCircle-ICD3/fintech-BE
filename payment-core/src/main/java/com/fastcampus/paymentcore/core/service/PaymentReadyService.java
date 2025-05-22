package com.fastcampus.paymentcore.core.service;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.SystemParameterUtil;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.ResponsePaymentReady;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.entity.TransactionStatus;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        nullCheckReadyPayment(paramMap);
        checkPaymentStatus(paramMap);

        String token = tokenHandler.generateTokenPaymentReady();
        paramMap.put("transactionToken", token);
        Long transactionId = saveTransaction(paramMap);

        LocalDateTime expiresAt = generateExpiresAt();
        return new ResponsePaymentReady(token, expiresAt);
    }

    private void nullCheckReadyPayment(Map<String, Object> paramMap) {
        List<String> targetKeys = List.of("merchantId", "merchantOrderId", "amount");
        for (String key : targetKeys) {
            if (!(paramMap.containsKey(key) && paramMap.get(key) != null)) {
                throw new HttpException(PaymentErrorCode.INVALID_PAYMENT_REQUEST);
            }
        }
    }

    private void checkPaymentStatus(Map<String, Object> paramMap) {
        Optional<Transaction> transactionOps = transactionRepository.findByTransactionToken((String) paramMap.get("transactionToken"));
        if (transactionOps.isPresent()) {
            Transaction transaction = transactionOps.get();
            if (!TransactionStatus.REQUESTED.equals(transaction.getStatus())) {
                throw new HttpException(PaymentErrorCode.DUPLICATE_ORDER);
            }
        }
    }

    private LocalDateTime generateExpiresAt() {
        Clock clock = Clock.system(ZoneId.of(zoneId));
        return LocalDateTime.now(clock).plusSeconds(Integer.parseInt(ttlQr));
    }

    private Long saveTransaction(Map<String, Object> paramMap) {
        Transaction transaction = new Transaction();
        transaction.setTransactionToken((String) paramMap.get("transactionToken"));
        transaction.setAmount(Long.parseLong(paramMap.get("amount").toString()));
        transaction.setMerchantId(Long.parseLong(paramMap.get("merchantId").toString()));
        transaction.setMerchantOrderId(paramMap.get("merchantOrderId").toString());
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction.setCreatedAt(LocalDateTime.now(Clock.system(ZoneId.of(zoneId))));

        Transaction result = transactionRepository.save(transaction);
        return result.getTransactionId();
    }
}

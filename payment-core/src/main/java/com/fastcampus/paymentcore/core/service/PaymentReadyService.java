package com.fastcampus.paymentcore.core.service;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.paymentcore.core.common.idem.Idempotent;
import com.fastcampus.paymentcore.core.common.util.SystemParameterUtil;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.ResponsePaymentReady;
import com.fastcampus.paymentcore.core.dummy.TransactionEntityDummy;
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

    Logger logger = LoggerFactory.getLogger(PaymentReadyService.class);

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TokenHandler tokenHandler;
    @Autowired
    SystemParameterUtil systemParameterUtil;

    @Value("${lifetime.qr}")
    private String ttlQr;
    @Value("${time.zoneId}")
    private String zoneId;

    /**
     * 결제 요청 api 에서 호출할 service 입니다.
     *
     * @param paramMap - transaction 객체를 생성에 필요한 데이터가 담겨 있는 parameter map (추후 논의 후 수정 가능) (예: amount, merchant_id, merchant_order_id 등)
     * @return qrToken - 요청받은 결제 정보를 transaction 객체로 저장하고, 저장한 transcation data 를 바탕으로 생성한 qr token
     */
    @Idempotent
    public ResponsePaymentReady readyPayment(Map<String, Object> paramMap) {
        // TODO - sdk key 검증하는 로직은 공통이라 aop 로 하든지 filter 로 하든지 해야 할 듯.
        // 그래서 endpoint 에 /api/* 이렇게 시작하나 보다 키 검증 대상 url 들 지정하려고.
        // 필수 parameter null check
        nullCheckReadyPayment(paramMap);
        // 이미 결제가 완료된 거래인지 status check
        checkPaymentStatus(paramMap);
        // token 생성
        String token = tokenHandler.generateTokenPaymentReady();
        // TODO - token + payment db 저장
        // 근데 이거 어느 테이블에 저장을 하지...?? transaction 에 저장하는 게 맞나 이거? db 새로 따야 하나 > 일단 transaction 에 저장
        paramMap.put("transactionToken", token);
        Long transactionId = saveTransaction(paramMap);
        // expired at 생성
        LocalDateTime expiresAt = generateExpiresAt();
        // response dto 생성
        ResponsePaymentReady paymentReady = new ResponsePaymentReady(token, expiresAt);
        return paymentReady;
    }

    private boolean nullCheckReadyPayment(Map<String, Object> paramMap) {
        // 필수 key 들 null check
        List<String> targetKeys = List.of("merchantId", "merchantOrderId", "amount");
        for (String key : targetKeys) {
            if(!(paramMap.containsKey(key) && paramMap.get(key) != null)) {
                throw new HttpException(PaymentErrorCode.INVALID_PAYMENT_REQUEST);
            }
        }
        return true;
    }

    private boolean checkPaymentStatus(Map<String, Object> paramMap) {
        // 해당 payment 가 이미 결제 완료된 상태인지 status 를 체크
        Optional<Transaction> transactionOps = transactionRepository.findByTransactionToken((String)paramMap.get("transactionToken"));
        if(transactionOps.isPresent()) {
            Transaction transaction = transactionOps.get();
            if( !(TransactionStatus.REQUESTED.equals(transaction.getStatus())) ) {
                throw new HttpException(PaymentErrorCode.DUPLICATE_ORDER);
            }
        }
        return true;
    }


    private LocalDateTime generateExpiresAt() {
        Clock clock = Clock.system(ZoneId.of(zoneId));
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expiresAt = now.plusSeconds(Integer.valueOf(ttlQr));
        return expiresAt;
    }

    private Long saveTransaction(Map<String, Object> paramMap) {
        // create entity
        Transaction transactionEntity = new Transaction();
        transactionEntity.setTransactionToken((String)paramMap.get("transactionToken"));
        transactionEntity.setAmount(Long.valueOf((String)paramMap.get("amount")));
        transactionEntity.setMerchantId(Long.valueOf((String)paramMap.get("merchantId")));
        transactionEntity.setMerchantOrderId((String)paramMap.get("merchantOrderId"));
        transactionEntity.setStatus(TransactionStatus.REQUESTED);

        Transaction result = transactionRepository.save(transactionEntity);
        return result.getTransactionId();
    }

}

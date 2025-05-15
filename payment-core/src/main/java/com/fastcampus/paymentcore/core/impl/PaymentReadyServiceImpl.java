package com.fastcampus.paymentcore.core.impl;

import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.CommonDto;
import com.fastcampus.paymentcore.core.dummy.TransactionEntityDummy;
import com.fastcampus.paymentcore.core.dummy.TransactionRepositoryDummy;
import com.fastcampus.paymentcore.core.service.PaymentReadyService;

import java.util.Map;

public class PaymentReadyServiceImpl implements PaymentReadyService {

    /**
     * 결제 요청 api 에서 호출할 service 입니다.
     *
     * @param paramMap - transaction 객체를 생성에 필요한 데이터가 담겨 있는 parameter map (추후 논의 후 수정 가능) (예: amount, merchant_id, merchant_order_id 등)
     * @return qrToken - 요청받은 결제 정보를 transaction 객체로 저장하고, 저장한 transcation data 를 바탕으로 생성한 qr token
     */
    @Override
    public String readyPayment(Map<String, Object> paramMap) {
        TransactionEntityDummy transaction = (TransactionEntityDummy)paramMap.get("key"); //TODO - TransactionEntityDummy -> TransactionEntity from infro
        TransactionRepositoryDummy transactionRepository = new TransactionRepositoryDummy();    //TODO - TransactionRepositoryDummy -> TransactionRepository from infro
        int transactionId = transactionRepository.save(transaction);
        TokenHandler tokenHandler = new TokenHandler(); // TODO - TokenHandler 를 interface 구현해야 하려나
        String qrToken = tokenHandler.encodeQrToken(transactionId);
        return qrToken;
    }
}

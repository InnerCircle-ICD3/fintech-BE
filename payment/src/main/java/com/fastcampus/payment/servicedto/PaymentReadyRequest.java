package com.fastcampus.payment.servicedto;

import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.entity.TransactionStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class PaymentReadyRequest extends IdempotencyDto {

    private final Long amount;
    private final Long merchantId;
    private final String merchantOrderId;

    private String transactionToken;


    public void nullCheckRequiredParam() {
        List<Object> targetList = Arrays.asList(amount, merchantId, merchantOrderId);

        boolean isNull = targetList.stream().anyMatch(obj -> Objects.isNull(obj));
        if (isNull) {
            throw new RuntimeException("파라미터 내용을 확인해 주세요");
        }
    }

    public Transaction convertToTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(this.getAmount());
        transaction.setMerchantId(this.getMerchantId());
        transaction.setMerchantOrderId(this.getMerchantOrderId());
        transaction.setStatus(TransactionStatus.REQUESTED);
        return transaction;
    }


}

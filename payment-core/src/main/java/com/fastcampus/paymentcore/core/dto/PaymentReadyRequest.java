package com.fastcampus.paymentcore.core.dto;

import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.common.exception.exception.BadRequestException;
import com.fastcampus.paymentcore.core.common.util.CommonUtil;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.entity.TransactionStatus;
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
            throw new BadRequestException(PaymentErrorCode.PAYMENT_READY_NULL_VALUE);
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

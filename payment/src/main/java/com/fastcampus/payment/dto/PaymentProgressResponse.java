package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.CardInfo;
import com.fastcampus.payment.entity.PaymentMethod;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class PaymentProgressResponse {


    private final TransactionStatus status;
    private final Long amount;
    private final String merchantId;
    private final String merchantOrderId;
    private final LocalDateTime createdAt;

    //  추가 필드들
    private final String transactionToken;
    private final CardInfo cardInfo;
    private final PaymentMethod paymentMethod;
    private final Boolean approvalResult;

    // 기존 생성자 유지 (하위 호환성)
    public PaymentProgressResponse(Transaction transaction) {
        this.status = transaction.getStatus();
        this.amount = transaction.getAmount();
        this.createdAt = transaction.getCreatedAt();
        this.merchantId = Long.toString(transaction.getMerchantId());
        this.merchantOrderId = transaction.getMerchantOrderId();
        this.transactionToken = transaction.getTransactionToken();
        this.cardInfo = null;  // 기본값
        this.paymentMethod = null;  // 기본값
        this.approvalResult = null;  // 기본값
    }

    // 확장된 생성자

    public String getStatus() {
        return this.status.name();}
}

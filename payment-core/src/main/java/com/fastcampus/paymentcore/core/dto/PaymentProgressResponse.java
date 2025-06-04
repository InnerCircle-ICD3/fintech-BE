package com.fastcampus.paymentcore.core.dto;

import com.fastcampus.paymentcore.core.entity.Transaction;
import com.fastcampus.paymentcore.core.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentProgressResponse {
    /**
     * 결제 진행 후 클라이언트에게 전달하는 값
     */
    private LocalDateTime createdAt;
    private String transactionToken;  // 외부 공개용 거래 식별자
    private TransactionStatus status;  //COMPLETED, FAILED, PENDING등
    private Long amount;
    private String merchantOrderId;
    private Long merchantId;

    public PaymentProgressResponse(Transaction transaction) {
        this.createdAt = transaction.getCreatedAt();
        this.transactionToken = transaction.getTransactionToken();
        this.status = transaction.getStatus();
        this.amount = transaction.getAmount();
        this.merchantOrderId = transaction.getMerchantOrderId();
        this.merchantId = transaction.getMerchantId();
    }
}

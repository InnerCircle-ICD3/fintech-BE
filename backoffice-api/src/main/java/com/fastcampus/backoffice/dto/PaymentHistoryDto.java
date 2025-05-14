package com.fastcampus.backoffice.dto;

import com.fastcampus.backoffice.entity.PaymentHistory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentHistoryDto {
    private String paymentId;
    private String transactionId;
    private String paymentMethod;
    private PaymentHistory.PaymentStatus paymentStatus;
    private BigDecimal paidAmount;
    private LocalDateTime approvedAt;
    private String failReason;
    private String lastTransactionId;
    private LocalDateTime createdAt;
} 
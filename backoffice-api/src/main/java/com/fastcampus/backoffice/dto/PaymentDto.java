package com.fastcampus.backoffice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDto {
    private Long paymentId;
    private Long transactionId;
    private Long userId;
    private Long paymentMethod;
    private String paymentStatus;
    private Long paidAmount;
    private LocalDateTime approvedAt;
    private String failReason;
    private Long lastTransactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
package com.fastcampus.backoffice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentSummaryDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalAmount;
    private BigDecimal completedAmount;
    private BigDecimal canceledAmount;
    private BigDecimal failedAmount;
    private long totalCount;
    private long completedCount;
    private long canceledCount;
    private long failedCount;
} 
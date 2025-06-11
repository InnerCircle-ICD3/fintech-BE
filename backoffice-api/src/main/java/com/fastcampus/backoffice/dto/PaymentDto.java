package com.fastcampus.backoffice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long paymentId;
    private String paymentToken;
    private Long userId;
    private Long merchantId;
    private String merchantOrderId;
    private String paymentStatus;
    private Long paidAmount;
    private Long lastTransactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 카드 정보 필드 추가
    private CardInfoDto cardInfo;
    
    @Data
    public static class CardInfoDto {
        private Long cardInfoId;
        private String type;
        private String last4;
        private String cardCompany;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
} 
package com.fastcampus.paymentmethod.entity;

/**
 * 결제 수단의 타입을 정의하는 Enum입니다.
 * 예: "CARD", "BANK_TRANSFER", "PAYPAL" 등
 */
public enum PaymentMethodType {
    CARD("카드", 100, 90),
    BANK_TRANSFER("계좌이체", 200, 95),
    MOBILE_PAY("모바일페이", 50, 98),
    CRYPTO("암호화폐", 500, 85),
    PAYPAL("페이팔", 150, 92),
    APPLE_PAY("애플페이", 80, 97),
    GOOGLE_PAY("구글페이", 80, 97);


    private final String displayName;
    private final int processingTimeMs;  // 처리 시간 (밀리초)
    private final int successRate;       // 성공률 (%)


    PaymentMethodType(String displayName, int processingTimeMs, int successRate) {
        this.displayName = displayName;
        this.processingTimeMs = processingTimeMs;
        this.successRate = successRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getProcessingTimeMs() {
        return processingTimeMs;
    }

    public int getSuccessRate() {
        return successRate;
    }
    /**
     * 문자열로부터 PaymentMethodType을 찾는 유틸리티 메서드
     */
    public static PaymentMethodType fromString(String type) {
        for (PaymentMethodType methodType : PaymentMethodType.values()) {
            if (methodType.name().equalsIgnoreCase(type)) {
                return methodType;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 결제 방식입니다: " + type);
    }

}

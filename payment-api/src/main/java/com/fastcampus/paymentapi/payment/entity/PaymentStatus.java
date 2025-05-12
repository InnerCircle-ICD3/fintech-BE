package com.fastcampus.paymentapi.payment.entity;

public enum PaymentStatus {
    READY,       // QR 생성됨 (결제 대기 중)
    USED,        // QR 스캔됨 (결제 시도 중)
    COMPLETED,   // 결제 완료
    FAILED       // 결제 실패
}
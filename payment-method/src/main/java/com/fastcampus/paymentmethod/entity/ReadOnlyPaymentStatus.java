package com.fastcampus.paymentmethod.entity;

/**
 * 결제 상태 enum (READ-ONLY 전용)
 * - 결제 상태를 문자열로 DB에 저장
 * - payment.status, transaction.status에 사용
 */
public enum ReadOnlyPaymentStatus {
    READY,
    REQUESTED,
    COMPLETED,
    FAILED,
    CANCELED;

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELED;
    }
}
package com.fastcampus.payment.entity;

/**
 * 거래 상태를 정의 하는 enum 클래스
 * -모든 거래(Transaction) 의 상태를 명확하게 구분하고 일관성 있게 관리하기 위한 enum
 * -db에는 문자열 (String)로 저장 (@Enumerated(EnumType.STRING) 사용)
 *
 * 상태 설명
 * - READY: 결제 준비 상태
 * - COMPLETED: 결제 성공
 * - FAILED: 결제 실패
 * - CANCELED: 결제 취소
 */
public enum TransactionStatus {
    READY,  //  결제 요청
    REQUESTED,  // 결제 조회
    COMPLETED,  // 결제 완료
    FAILED, // 결제 실패
    CANCELED;   // 결제 취소

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELED;
    }
}
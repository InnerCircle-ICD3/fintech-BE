package com.fastcampus.paymentcore.core.entity;

public enum TransactionStatus {
    REQUESTED,       // 거래 요청 됨
    PROCESS,       // 거래 진행 중
    COMPLETED,   // 거래 완료
    FAILED       // 거래 실패
}

package com.fastcampus.paymentcore.core.dummy;

public class TransactionRepositoryDummy {
    // infra module 에 있는 transaction entity 사용하기
    public int save(TransactionEntityDummy transactionEntity) {
        return 0;
    }
    public TransactionEntityDummy find(int transactionId) {
        return null;
    }
}

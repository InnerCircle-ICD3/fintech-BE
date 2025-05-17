package com.fastcampus.paymentcore.core.dummy;

import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepositoryDummy {
    // infra module 에 있는 transaction entity 사용하기
    public int save(TransactionEntityDummy transactionEntity) {
        return 0;
    }
    public TransactionEntityDummy find(int transactionId) {
        return null;
    }
}

package com.fastcampus.paymentcore.core.repository;

import com.fastcampus.paymentcore.core.entity.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    public abstract Optional<Transaction> findByTransactionToken(String transactionToken);
    public abstract Transaction save(Transaction transaction);
    public abstract Optional<Transaction> findById(Long id);

}

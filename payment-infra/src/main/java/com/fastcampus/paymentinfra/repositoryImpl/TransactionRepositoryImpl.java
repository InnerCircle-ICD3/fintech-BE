package com.fastcampus.paymentinfra.repositoryImpl;

import com.fastcampus.paymentcore.core.entity.Transaction;
import com.fastcampus.paymentcore.core.repository.TransactionRepository;
import com.fastcampus.paymentinfra.repository.TransactionRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {
    private final TransactionRepositoryJpa transactionRepositoryJpa;


    @Override
    public Optional<Transaction> findByTransactionToken(String transactionToken) {
        return transactionRepositoryJpa.findByTransactionToken(transactionToken);
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepositoryJpa.save(transaction);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepositoryJpa.findById(id);
    }
}

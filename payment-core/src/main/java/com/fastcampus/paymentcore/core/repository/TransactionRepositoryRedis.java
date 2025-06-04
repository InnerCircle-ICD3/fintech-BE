package com.fastcampus.paymentcore.core.repository;

import com.fastcampus.paymentcore.core.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface TransactionRepositoryRedis {
    public Optional<Transaction> findByToken(String token);
    public void save(Transaction transaction, long ttlSeconds);
    public void update(Transaction transaction);
    public void delete(String token);
}



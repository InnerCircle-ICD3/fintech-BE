package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepositoryJpa extends JpaRepository<Transaction, Long>, TransactionRepository {
//    Optional<Transaction> findByMerchantOrderId(String merchantOrderId);
//
//    List<Transaction> findByMerchantId(Long merchantId);
    Optional<Transaction> findByTransactionToken(String transactionToken);
}

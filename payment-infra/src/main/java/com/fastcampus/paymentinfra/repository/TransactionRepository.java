package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByMerchantOrderId(String merchantOrderId);

    List<Transaction> findByMerchantId(Long merchantId);
    Optional<Transaction> findByTransactionToken(String transactionToken);


}

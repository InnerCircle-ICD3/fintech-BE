package com.fastcampus.paymentmethod.repository;

import com.fastcampus.paymentmethod.entity.ReadOnlyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadOnlyTransactionRepository extends JpaRepository<ReadOnlyTransaction, Long> {
    List<ReadOnlyTransaction> findByPayment_User_UserId(Long userId);
    List<ReadOnlyTransaction> findByCardToken(String cardToken);
}
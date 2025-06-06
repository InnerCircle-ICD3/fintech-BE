package com.fastcampus.payment.repository;


import com.fastcampus.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId")
    List<Payment> findByUserId(Long userId);

    @Query("SELECT p FROM Payment p WHERE p.transactionId = :transactionId")
    List<Payment> findByTransactionId(Long transactionId);

    List<Payment> findByPaymentStatus(String paymentStatus);

    @Query("SELECT p FROM Payment p JOIN FETCH p.cardInfo WHERE p.userId = :userId")
    List<Payment> findByUserIdWithCardInfo(Long userId);
}

package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);

    List<Payment> findByTransactionId(Long transactionId);

    List<Payment> findByPaymentStatus(String paymentStatus);
}

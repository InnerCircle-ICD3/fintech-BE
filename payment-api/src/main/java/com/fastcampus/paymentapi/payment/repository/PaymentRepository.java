package com.fastcampus.paymentapi.payment.repository;

import com.fastcampus.paymentapi.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByMerchantIdAndMerchantOrderId(Long merchantId, Long merchantOrderId);
}
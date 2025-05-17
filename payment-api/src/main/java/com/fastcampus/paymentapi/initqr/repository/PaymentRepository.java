package com.fastcampus.paymentapi.initqr.repository;

import com.fastcampus.paymentapi.initqr.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByMerchantIdAndMerchantOrderId(Long merchantId, Long merchantOrderId);
    Optional<Payment> findByQrToken(String qrToken);
}
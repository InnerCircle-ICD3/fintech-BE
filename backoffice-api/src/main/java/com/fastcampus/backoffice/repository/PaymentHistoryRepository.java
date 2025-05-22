package com.fastcampus.backoffice.repository;

import com.fastcampus.backoffice.entity.PaymentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Page<PaymentHistory> findByMerchant_MerchantId(Long merchantId, Pageable pageable);
    Optional<PaymentHistory> findByPaymentId(String paymentId);
    Optional<PaymentHistory> findByTransactionId(String transactionId);
    Page<PaymentHistory> findByMerchant_MerchantIdAndPaymentStatusAndApprovedAtBetween(
        Long merchantId, 
        PaymentHistory.PaymentStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
    Page<PaymentHistory> findByMerchant_MerchantIdAndApprovedAtBetween(
        Long merchantId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
} 
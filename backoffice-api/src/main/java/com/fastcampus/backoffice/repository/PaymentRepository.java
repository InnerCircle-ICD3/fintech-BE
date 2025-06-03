package com.fastcampus.backoffice.repository;

import com.fastcampus.paymentinfra.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.cardInfo " +
           "WHERE p.merchantId = :merchantId " +
           "AND (:status IS NULL OR p.paymentStatus = :status) " +
           "AND p.approvedAt BETWEEN :startDate AND :endDate")
    Page<Payment> findPaymentHistoryWithOptionalStatus(
            @Param("merchantId") Long merchantId,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.cardInfo " +
           "WHERE p.transactionId = :transactionId")
    Optional<Payment> findByTransactionId(@Param("transactionId") Long transactionId);
}


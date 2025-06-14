package com.fastcampus.backoffice.repository;

import com.fastcampus.payment.entity.Payment;
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
    /**
     * 지정된 가맹점 ID와 승인일자 범위에 해당하는 결제 내역을 페이징하여 조회합니다.
     * 결제 상태가 지정된 경우 해당 상태로도 필터링하며, 관련 카드 정보(cardInfo)를 함께 조회합니다.
     *
     * @param merchantId 결제 내역을 조회할 가맹점의 ID
     * @param status     (선택) 필터링할 결제 상태, null이면 모든 상태 포함
     * @param startDate  승인일자 시작 범위
     * @param endDate    승인일자 종료 범위
     * @param pageable   페이징 및 정렬 정보
     * @return 조건에 맞는 결제 내역의 페이지 객체
     */
    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.lastTransaction t " +
           "LEFT JOIN FETCH t.paymentMethod " +
           "WHERE p.merchantId = :merchantId " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND p.createdAt BETWEEN :startDate AND :endDate")
    Page<Payment> findPaymentHistoryWithOptionalStatus(
            @Param("merchantId") Long merchantId,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * 주어진 거래 ID에 해당하는 결제 정보를 카드 정보와 함께 조회합니다.
     *
     * @param transactionId 조회할 결제의 거래 ID
     * @return 거래 ID에 해당하는 결제 정보가 존재하면 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.lastTransaction t " +
           "LEFT JOIN FETCH t.paymentMethod " +
           "WHERE p.paymentToken = :paymentToken")
    Optional<Payment> findByPaymentToken(String paymentToken);
}


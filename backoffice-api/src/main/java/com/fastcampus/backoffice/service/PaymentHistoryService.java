package com.fastcampus.backoffice.service;

import com.fastcampus.backoffice.dto.PaymentHistoryDto;
import com.fastcampus.backoffice.dto.PaymentSummaryDto;
import com.fastcampus.backoffice.entity.PaymentHistory;
import com.fastcampus.backoffice.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional(readOnly = true)
    public Page<PaymentHistoryDto> getPaymentHistories(Long merchantId, Pageable pageable) {
        return paymentHistoryRepository.findByMerchant_MerchantId(merchantId, pageable)
            .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<PaymentHistoryDto> getPaymentHistory(String paymentId) {
        return paymentHistoryRepository.findByPaymentId(paymentId)
            .map(this::convertToDto);
    }

    @Transactional
    public PaymentHistoryDto cancelPayment(String paymentId) {
        PaymentHistory payment = paymentHistoryRepository.findByPaymentId(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentHistory.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be canceled");
        }

        payment.setPaymentStatus(PaymentHistory.PaymentStatus.CANCELED);
        payment.setLastTransactionId(payment.getTransactionId());
        payment.setTransactionId(UUID.randomUUID().toString());
        
        return convertToDto(paymentHistoryRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public Page<PaymentHistoryDto> getSettlementHistories(
        Long merchantId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    ) {
        return paymentHistoryRepository
            .findByMerchant_MerchantIdAndPaymentStatusAndApprovedAtBetween(
                merchantId,
                PaymentHistory.PaymentStatus.COMPLETED,
                startDate,
                endDate,
                pageable
            )
            .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public PaymentSummaryDto getPaymentSummary(
        Long merchantId,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        PaymentSummaryDto summary = new PaymentSummaryDto();
        summary.setStartDate(startDate);
        summary.setEndDate(endDate);

        // 전체 결제 내역 조회
        Page<PaymentHistory> allPayments = paymentHistoryRepository
            .findByMerchant_MerchantIdAndApprovedAtBetween(merchantId, startDate, endDate, Pageable.unpaged());

        // 상태별 통계 계산
        allPayments.forEach(payment -> {
            summary.setTotalAmount(summary.getTotalAmount().add(payment.getPaidAmount()));
            summary.setTotalCount(summary.getTotalCount() + 1);

            switch (payment.getPaymentStatus()) {
                case COMPLETED:
                    summary.setCompletedAmount(summary.getCompletedAmount().add(payment.getPaidAmount()));
                    summary.setCompletedCount(summary.getCompletedCount() + 1);
                    break;
                case CANCELED:
                    summary.setCanceledAmount(summary.getCanceledAmount().add(payment.getPaidAmount()));
                    summary.setCanceledCount(summary.getCanceledCount() + 1);
                    break;
                case FAILED:
                    summary.setFailedAmount(summary.getFailedAmount().add(payment.getPaidAmount()));
                    summary.setFailedCount(summary.getFailedCount() + 1);
                    break;
            }
        });

        return summary;
    }

    private PaymentHistoryDto convertToDto(PaymentHistory payment) {
        PaymentHistoryDto dto = new PaymentHistoryDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaidAmount(payment.getPaidAmount());
        dto.setApprovedAt(payment.getApprovedAt());
        dto.setFailReason(payment.getFailReason());
        dto.setLastTransactionId(payment.getLastTransactionId());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
} 
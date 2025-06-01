package com.fastcampus.backoffice.service;

import com.fastcampus.backoffice.dto.PaymentDto;
import com.fastcampus.backoffice.repository.PaymentRepository;
import com.fastcampus.paymentinfra.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Page<PaymentDto> getPaymentHistory(
            Long merchantId,
            String status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        return paymentRepository.findPaymentHistoryWithOptionalStatus(
                merchantId, status, startDate, endDate, pageable
        ).map(this::convertToDto);
    }

    public Optional<PaymentDto> getPaymentDetail(Long transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .map(this::convertToDto);
    }

    private PaymentDto convertToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setUserId(payment.getUserId());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaidAmount(payment.getPaidAmount());
        dto.setApprovedAt(payment.getApprovedAt());
        dto.setFailReason(payment.getFailReason());
        dto.setLastTransactionId(payment.getLastTransactionId());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
} 
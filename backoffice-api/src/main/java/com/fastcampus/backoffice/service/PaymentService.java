package com.fastcampus.backoffice.service;

import com.fastcampus.backoffice.dto.PaymentDto;
import com.fastcampus.backoffice.repository.PaymentRepository;
import com.fastcampus.payment.entity.Payment;
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

    public Optional<PaymentDto> getPaymentDetail(String paymentToken) {
        return paymentRepository.findByPaymentToken(paymentToken)
                .map(this::convertToDto);
    }

    /**
     * Payment 엔티티를 PaymentDto로 변환합니다.
     *
     * 결제 정보와 함께 카드 정보가 포함되어 있을 경우, 카드 정보도 CardInfoDto로 변환하여 포함합니다.
     *
     * @param payment 변환할 Payment 엔티티
     * @return 변환된 PaymentDto 객체
     */
    private PaymentDto convertToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getId());
        dto.setPaymentToken(payment.getPaymentToken());
        dto.setUserId(payment.getUserId());
        dto.setMerchantId(payment.getMerchantId());
        dto.setMerchantOrderId(payment.getMerchantOrderId());
        dto.setPaymentStatus(payment.getStatus().name());
        dto.setPaidAmount(payment.getTotalAmount());
        dto.setLastTransactionId(payment.getLastTransaction() != null ? payment.getLastTransaction().getId() : null);
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        
        return dto;
    }
} 
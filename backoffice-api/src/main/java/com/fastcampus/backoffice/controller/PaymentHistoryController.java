package com.fastcampus.backoffice.controller;

import com.fastcampus.backoffice.dto.PaymentHistoryDto;
import com.fastcampus.backoffice.dto.PaymentSummaryDto;
import com.fastcampus.backoffice.service.PaymentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/merchants/payment-histories")
@RequiredArgsConstructor
@Tag(name = "Payment History Management", description = "Payment history management endpoints")
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/{merchantId}")
    @Operation(summary = "Get payment histories for merchant")
    public ResponseEntity<Page<PaymentHistoryDto>> getPaymentHistories(
        @PathVariable Long merchantId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(paymentHistoryService.getPaymentHistories(merchantId, pageable));
    }

    @GetMapping("/{merchantId}/{paymentId}")
    @Operation(summary = "Get payment history details")
    public ResponseEntity<PaymentHistoryDto> getPaymentHistory(
        @PathVariable Long merchantId,
        @PathVariable String paymentId
    ) {
        return paymentHistoryService.getPaymentHistory(paymentId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{merchantId}/{paymentId}/cancel")
    @Operation(summary = "Cancel payment")
    public ResponseEntity<PaymentHistoryDto> cancelPayment(
        @PathVariable Long merchantId,
        @PathVariable String paymentId
    ) {
        return ResponseEntity.ok(paymentHistoryService.cancelPayment(paymentId));
    }

    @GetMapping("/{merchantId}/settlement-summaries")
    @Operation(summary = "Get settlement histories for merchant")
    public ResponseEntity<Page<PaymentHistoryDto>> getSettlementHistories(
        @PathVariable Long merchantId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            paymentHistoryService.getSettlementHistories(merchantId, startDate, endDate, pageable)
        );
    }

    @GetMapping("/{merchantId}/payment-summaries")
    @Operation(summary = "Get payment statistics for merchant")
    public ResponseEntity<PaymentSummaryDto> getPaymentSummary(
        @PathVariable Long merchantId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return ResponseEntity.ok(
            paymentHistoryService.getPaymentSummary(merchantId, startDate, endDate)
        );
    }
} 
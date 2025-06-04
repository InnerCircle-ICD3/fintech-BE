package com.fastcampus.backoffice.controller;

import com.fastcampus.backoffice.dto.PaymentDto;
import com.fastcampus.backoffice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/merchants/payment-histories")
@RequiredArgsConstructor
@Tag(name = "Payment History Management", description = "Payment history management endpoints")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "결제내역 전체 조회")
    public ResponseEntity<Page<PaymentDto>> getPaymentHistory(
            @RequestParam Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(
                merchantId, status, startDate.atStartOfDay(), endDate.atStartOfDay(), pageable
        ));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "결제내역 상세 조회")
    public ResponseEntity<PaymentDto> getPaymentDetail(
            @PathVariable Long transactionId
    ) {
        Optional<PaymentDto> payment = paymentService.getPaymentDetail(transactionId);
        return payment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
} 
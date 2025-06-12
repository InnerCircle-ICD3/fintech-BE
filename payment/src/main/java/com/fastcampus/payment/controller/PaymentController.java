package com.fastcampus.payment.controller;

import com.fastcampus.payment.dto.*;
import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.service.PaymentCancelService;
import com.fastcampus.payment.service.PaymentExecutionService;
import com.fastcampus.payment.service.PaymentProgressService;
import com.fastcampus.payment.service.PaymentReadyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PaymentController {

    private final PaymentReadyService paymentReadyService;
    private final PaymentProgressService paymentProgressService;
    private final PaymentExecutionService paymentExecutionService;
    private final PaymentCancelService paymentCancelService;

    /**
     * 1. 결제 요청 처리
     * - 거래 생성 및 QR Token 반환
     */
    @PostMapping
    public PaymentReadyResponse readyPayment(@RequestBody @Valid PaymentReadyRequest request) {
        Payment payment = paymentReadyService.readyPayment(request.convertToPayment());
        return new PaymentReadyResponse(payment);
    }

    /**
     * 2. QR 코드 거래 상태 조회
     * - payment_token으로 거래 상태 확인
     */
    @GetMapping("/{paymentToken}")
    public PaymentProgressResponse progressPayment(@NotBlank @PathVariable("paymentToken") String paymentToken) {
        Payment payment = paymentProgressService.progressPayment(paymentToken);
        PaymentProgressResponse response = new PaymentProgressResponse(payment);
        return response;
    }

    /**
     * 3. 결제 실행
     * - payment_token + card_token 이용해 결제 처리
     */
    @PatchMapping
    public PaymentExecutionResponse executePayment(@RequestBody @Valid PaymentExecutionRequest request) {
        PaymentExecutionResponse response = paymentExecutionService.execute(request);
        return response;
    }


    /**
     * 4. 결제 취소
     * - transaction_token 으로 결제 정보 식별하여 결제 취소
     */
    @DeleteMapping("/{paymentToken}")
    public PaymentCancelResponse cancelPayment(@NotBlank @PathVariable("paymentToken") String paymentToken) {
        Payment payment = paymentCancelService.cancelPayment(paymentToken);
        PaymentCancelResponse response = new PaymentCancelResponse(payment);
        return response;
    }
}

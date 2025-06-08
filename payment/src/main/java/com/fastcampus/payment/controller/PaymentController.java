package com.fastcampus.payment.controller;

import com.fastcampus.payment.dto.*;
import com.fastcampus.payment.entity.Payment;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PaymentController {

    private final PaymentReadyService paymentReadyService;
    private final PaymentProgressService paymentProgressService;
    private final PaymentExecutionService paymentExecutionService;

    /**
     * 1. 결제 요청 처리
     * - 거래 생성 및 QR Token 반환
     */
    @PostMapping("/transactions/ready")
    public PaymentReadyResponse initiateTransaction(@RequestBody @Valid PaymentReadyRequest request) {
        Payment payment = paymentReadyService.readyPayment(request.convertToPayment());
        return new PaymentReadyResponse(payment);
    }

    /**
     * 2. QR 코드 거래 상태 조회
     * - transaction_token으로 거래 상태 확인
     */
    @GetMapping("/transactions/{token}")
    public PaymentProgressResponse getTransactionProgress(@NotBlank @PathVariable String token) {
        Payment payment = paymentProgressService.progressPayment(token);
        PaymentProgressResponse response = new PaymentProgressResponse(payment);
        return response;
    }

    /**
     * 3. 결제 실행
     * - transaction_token + card_token 이용해 결제 처리
     */
    @PostMapping("/payments/execute")
    public PaymentExecutionResponse executePayment(@RequestBody @Valid PaymentExecutionRequest request) {
        PaymentExecutionResponse response = paymentExecutionService.execute(request);
        return response;
    }
}

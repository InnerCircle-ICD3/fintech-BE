package com.fastcampus.paymentapi.payment.controller;

import com.fastcampus.paymentcore.core.dto.*;
import com.fastcampus.paymentcore.core.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentReadyService paymentReadyService;
    private final PaymentProgressService paymentProgressService;
    private final PaymentExecutionService paymentExecutionService;

    /**
     * 1. 결제 요청 처리
     * - 거래 생성 및 QR Token 반환
     */
    @PostMapping("/transactions/initiate")
    public ResponsePaymentReady initiateTransaction(@RequestBody Map<String, Object> paramMap) {
        return paymentReadyService.readyPayment(paramMap);
    }

    /**
     * 2. QR 코드 거래 상태 조회
     * - transaction_token으로 거래 상태 확인
     */
    @GetMapping("/transactions/{token}")
    public PaymentProgressDto getTransactionProgress(@PathVariable("token") String token) {
        return paymentProgressService.progressPayment(token);
    }

    /**
     * 3. 결제 실행
     * - transaction_token + card_token 이용해 결제 처리
     */
    @PostMapping("/payments/execute")
    public PaymentProgressResponse executePayment(@RequestBody PaymentProgressRequest request) {
        return paymentExecutionService.execute(request);
    }
}

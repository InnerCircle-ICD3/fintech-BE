package com.fastcampus.paymentapi.payment.controller;

import com.fastcampus.paymentapi.payment.dto.PaymentRequest;
import com.fastcampus.paymentapi.payment.dto.QrUrlResponse;
import com.fastcampus.paymentapi.payment.dto.QrVerificationResponse;
import com.fastcampus.paymentapi.payment.service.PaymentService;
import com.fastcampus.paymentapi.sdk.service.SdkKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final SdkKeyService sdkKeyService;
    private final PaymentService paymentService;

    /**
     * ✅ 결제 요청 - QR Url 지급 (이미지 X)
     */
    @PostMapping("/request")
    public QrUrlResponse requestPayment(@Valid @RequestBody PaymentRequest request) {
        sdkKeyService.validateOwnershipOrThrow(request.getSdkKey(), request.getMerchantId());

        String qrUrl = paymentService.createQr(request);
        return new QrUrlResponse(qrUrl);
    }

    /**
     * ✅ QR 상태 확인 - QrStatus 응답
     */
    @GetMapping("/verify")
    public QrVerificationResponse verifyQr(@RequestParam String token) {
        return paymentService.verifyQrStatus(token);
    }
}

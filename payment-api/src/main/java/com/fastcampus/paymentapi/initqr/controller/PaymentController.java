package com.fastcampus.paymentapi.initqr.controller;

import com.fastcampus.paymentapi.initqr.dto.QrInitRequest;
import com.fastcampus.paymentapi.initqr.dto.QrInitResponse;
import com.fastcampus.paymentapi.initqr.dto.QrVerificationResponse;
import com.fastcampus.paymentapi.initqr.service.PaymentService;
import com.fastcampus.paymentapi.sdk.service.SdkKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class PaymentController {

    private final SdkKeyService sdkKeyService;
    private final PaymentService paymentService;

    /**
     * ✅ 결제 요청 - QR Url 지급 (이미지 X)
     */
    @PostMapping("/initiate")
    public QrInitResponse requestPayment(@Valid @RequestBody QrInitRequest request) {
        sdkKeyService.validateOwnershipOrThrow(request.getSdkKey(), request.getMerchantId());

        return paymentService.createQr(request);
    }

    /**
     * ✅ QR 상태 확인 - QrStatus 응답
     */
    @GetMapping("/{token}")
    public QrVerificationResponse verifyQr(@RequestParam String token) {
        return paymentService.verifyQrStatus(token);
    }
}

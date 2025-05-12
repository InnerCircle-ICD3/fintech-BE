package com.fastcampus.paymentapi.payment.controller;

import com.fastcampus.common.exception.BaseException;
import com.fastcampus.common.exception.PaymentErrorCode;
import com.fastcampus.common.exception.SdkErrorCode;
import com.fastcampus.paymentapi.payment.dto.PaymentRequest;
import com.fastcampus.paymentapi.payment.dto.QrImageResponse;
import com.fastcampus.paymentapi.payment.service.PaymentService;
import com.fastcampus.paymentapi.payment.utils.QrUtils;
import com.fastcampus.paymentapi.sdk.service.SdkKeyService;
import com.google.zxing.WriterException;
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
     * ✅ 결제 요청 - QR 이미지 지급
     */
    @PostMapping("/request")
    public QrImageResponse requestPayment(@Valid @RequestBody PaymentRequest request) {
        boolean isValid = sdkKeyService.verifyKeyOwnership(
                request.getSdkKey(),
                request.getMerchantId()
        );

        if (!isValid) {
            throw new BaseException(SdkErrorCode.INVALID_SDK_KEY);
        }

        // QR URL 생성 및 결제 저장
        String qrUrl = paymentService.createQr(request);

        // QR 이미지 생성 (예외는 내부에서 BaseException으로 변환됨)
        String qrBase64 = QrUtils.generateQrCodeBase64(qrUrl, 250, 250);
        return new QrImageResponse(qrBase64);
    }
}

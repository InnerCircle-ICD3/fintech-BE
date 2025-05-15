package com.fastcampus.paymentapi.sdk.controller;

import com.fastcampus.common.exception.HttpException;
import com.fastcampus.common.exception.customerror.SdkErrorCode;
import com.fastcampus.paymentapi.sdk.dto.SdkCheckRequest;
import com.fastcampus.paymentapi.sdk.dto.SdkCheckResponse;
import com.fastcampus.paymentapi.sdk.dto.SdkIssueRequest;
import com.fastcampus.paymentapi.sdk.dto.SdkIssueResponse;
import com.fastcampus.paymentapi.sdk.service.SdkKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sdk")
@RequiredArgsConstructor
public class SdkKeyController {

    private final SdkKeyService sdkKeyService;

    /**
     * ✅ SDK 키 발급
     */
    @PostMapping("/issue")
    public ResponseEntity<SdkIssueResponse> issueSdkKey(@Valid @RequestBody SdkIssueRequest request) {
        SdkIssueResponse response = sdkKeyService.issueSdkKey(request.getMerchantId());
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ SDK 키 유효성 확인
     */
    @PostMapping("/check")
    public ResponseEntity<SdkCheckResponse> checkSdkKey(@RequestBody SdkCheckRequest request) {
        if (request.getSdkKey() == null || request.getSdkKey().isBlank()) {
            throw new HttpException(SdkErrorCode.INVALID_SDK_KEY); // ❗ 400 Bad Request → 더 명확한 예외로 변경
        }

        SdkCheckResponse response = sdkKeyService.checkSdkKey(request.getSdkKey());
        return ResponseEntity.ok(response);
    }
}
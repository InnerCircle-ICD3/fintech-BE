package com.fastcampus.paymentapi.controller;

import com.fastcampus.paymentapi.dto.SdkIssueRequest;
import com.fastcampus.paymentapi.dto.SdkIssueResponse;
import com.fastcampus.paymentapi.service.SdkKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sdk")
@RequiredArgsConstructor
public class SdkKeyController {

    private final SdkKeyService sdkKeyService;

    @PostMapping("/issue")
    public ResponseEntity<SdkIssueResponse> issueSdkKey(@RequestBody SdkIssueRequest request) {
        SdkIssueResponse response = sdkKeyService.issueSdkKey(request.getMerchantId());
        return ResponseEntity.ok(response);
    }
}

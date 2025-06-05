package com.fastcampus.backofficemanage.controller;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.sdk.SdkKeyResponse;
import com.fastcampus.backofficemanage.service.SdkKeyService;
import com.fastcampus.common.util.SwaggerDocs.StandardResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sdk-key")
@RequiredArgsConstructor
public class SdkKeyController {

    private final SdkKeyService sdkKeyService;

    @Operation(summary = "내 SDK Key 조회")
    @StandardResponses
    @GetMapping
    public ResponseEntity<SdkKeyResponse> getSdkKey(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        String sdkKey = sdkKeyService.getSdkKey(authorization);
        return ResponseEntity.ok(new SdkKeyResponse(sdkKey));
    }

    @Operation(summary = "SDK Key 비활성화")
    @StandardResponses
    @PostMapping("/deactivate")
    public ResponseEntity<CommonResponse> deactivateSdkKey(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        sdkKeyService.deactivateSdkKey(authorization);
        return ResponseEntity.ok(CommonResponse.success("SDK Key가 비활성화되었습니다."));
    }

    @Operation(summary = "SDK Key 활성화")
    @StandardResponses
    @PostMapping("/activate")
    public ResponseEntity<CommonResponse> activateSdkKey(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        sdkKeyService.activateSdkKey(authorization);
        return ResponseEntity.ok(CommonResponse.success("SDK Key가 활성화되었습니다."));
    }
}

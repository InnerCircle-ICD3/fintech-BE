package com.fastcampus.backofficemanage.controller;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.sdk.SdkKeyResponse;
import com.fastcampus.backofficemanage.service.SdkKeyService;
import com.fastcampus.common.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sdk-key")
@RequiredArgsConstructor
public class SdkKeyController {

    private final SdkKeyService sdkKeyService;

    @Operation(summary = "내 SDK Key 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Key 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<SdkKeyResponse> getSdkKey(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        String sdkKey = sdkKeyService.getSdkKey(authorization);
        return ResponseEntity.ok(new SdkKeyResponse(sdkKey));
    }

    @Operation(summary = "SDK Key 비활성화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비활성화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Key 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/deactivate")
    public ResponseEntity<CommonResponse> deactivateSdkKey(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        sdkKeyService.deactivateSdkKey(authorization);
        return ResponseEntity.ok(CommonResponse.success("SDK Key가 비활성화되었습니다."));
    }

    @Operation(summary = "SDK Key 활성화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활성화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Key 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/activate")
    public ResponseEntity<CommonResponse> activateSdkKey(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        sdkKeyService.activateSdkKey(authorization);
        return ResponseEntity.ok(CommonResponse.success("SDK Key가 활성화되었습니다."));
    }

    @Operation(summary = "SDK Key 유효성 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Key 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/validate")
    public ResponseEntity<CommonResponse> validateSdkKey(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        boolean isValid = sdkKeyService.isSdkKeyValid(authorization);
        return ResponseEntity.ok(CommonResponse.success(isValid ? "SDK Key가 유효합니다." : "SDK Key가 유효하지 않습니다."));
    }

    @Operation(summary = "SDK Key 재발급 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Key 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/regenerate")
    public ResponseEntity<CommonResponse> regenerateSdkKey(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        String newSdkKey = sdkKeyService.regenerateSdkKey(authorization);
        return ResponseEntity.ok(CommonResponse.success("SDK Key가 재발급되었습니다. 새로운 Key: " + newSdkKey));
    }
}

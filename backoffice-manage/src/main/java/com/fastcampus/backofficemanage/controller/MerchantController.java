package com.fastcampus.backofficemanage.controller;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.info.MerchantInfoResponse;
import com.fastcampus.backofficemanage.dto.login.request.MerchantLoginRequest;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.dto.signup.request.MerchantSignUpRequest;
import com.fastcampus.backofficemanage.dto.signup.response.MerchantSignUpResponse;
import com.fastcampus.backofficemanage.dto.update.request.MerchantUpdateRequest;
import com.fastcampus.backofficemanage.dto.update.request.UpdatePasswordRequest;
import com.fastcampus.backofficemanage.dto.update.response.MerchantUpdateResponse;
import com.fastcampus.backofficemanage.service.AuthService;
import com.fastcampus.backofficemanage.service.MerchantService;
import com.fastcampus.common.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final AuthService authService;

    @Operation(summary = "가맹점 회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 형식",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "InvalidRequest",
                                    summary = "입력 유효성 실패",
                                    value = """
                {
                  "code": "VALIDATION_ERROR",
                  "message": "이메일 형식이 올바르지 않습니다.",
                  "status": 400,
                  "path": "/merchants/register",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복 로그인 ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "DuplicateLoginId",
                                    summary = "이미 사용 중인 로그인 ID",
                                    value = """
                {
                  "code": "DUPLICATE_LOGIN_ID",
                  "message": "이미 존재하는 로그인 ID입니다.",
                  "status": 409,
                  "path": "/merchants/register",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "InternalServerError",
                                    summary = "예상치 못한 서버 오류",
                                    value = """
                {
                  "code": "INTERNAL_SERVER_ERROR",
                  "message": "서버 오류가 발생했습니다.",
                  "status": 500,
                  "path": "/merchants/register",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<MerchantSignUpResponse> register(
            @RequestBody @Valid MerchantSignUpRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @Operation(summary = "가맹점 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 로그인 정보",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "비활성화 계정",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<MerchantLoginResponse> login(
            @RequestBody @Valid MerchantLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Header에 있는 Token을 통한 가맹점 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 없음 또는 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "가맹점 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/info")
    public ResponseEntity<MerchantInfoResponse> getInfo(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(merchantService.getMyInfoByToken(authorizationHeader));
    }

    @Operation(summary = "가맹점 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 없음 또는 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복 사업자번호",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/modify")
    public ResponseEntity<MerchantUpdateResponse> updateInfo(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid MerchantUpdateRequest request
    ) {
        return ResponseEntity.ok(merchantService.updateMyInfo(authorization, request));
    }

    @Operation(summary = "비밀번호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치 또는 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UpdatePasswordRequest request
    ) {
        merchantService.updatePassword(authorization, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가맹점 삭제(Soft Delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 없음 또는 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "가맹점 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> delete(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(merchantService.deleteMyAccount(authorizationHeader));
    }

    @Operation(summary = "가맹점 로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 없음 또는 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return authService.logout(token);
    }

    @Operation(summary = "AccessToken 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "RefreshToken 누락/만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reissue")
    public ResponseEntity<MerchantLoginResponse> reissue(
            @Parameter(hidden = true) @RequestHeader("Refresh-Token") String refreshToken) {
        return authService.reissue(refreshToken);
    }
}

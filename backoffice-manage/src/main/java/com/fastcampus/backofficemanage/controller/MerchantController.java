package com.fastcampus.backofficemanage.controller;

import com.fastcampus.backofficemanage.aspect.CurrentLoginId;
import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.info.MerchantInfoResponse;
import com.fastcampus.backofficemanage.dto.login.request.MerchantLoginRequest;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.dto.signup.request.MerchantSignUpRequest;
import com.fastcampus.backofficemanage.dto.signup.response.MerchantSignUpResponse;
import com.fastcampus.backofficemanage.dto.update.request.MerchantUpdateRequest;
import com.fastcampus.backofficemanage.dto.update.response.MerchantUpdateResponse;
import com.fastcampus.backofficemanage.service.AuthService;
import com.fastcampus.backofficemanage.service.MerchantService;
import com.fastcampus.common.util.SwaggerDocs.StandardResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @StandardResponses
    @PostMapping("/register")
    public ResponseEntity<MerchantSignUpResponse> register(@RequestBody @Valid MerchantSignUpRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @Operation(summary = "가맹점 로그인")
    @StandardResponses
    @PostMapping("/login")
    public ResponseEntity<MerchantLoginResponse> login(@RequestBody @Valid MerchantLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Header에 있는 Token을 통한 가맹점 정보 조회")
    @StandardResponses
    @GetMapping("/info")
    public ResponseEntity<MerchantInfoResponse> getInfo(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(merchantService.getMyInfoByToken(authorizationHeader));
    }

    @Operation(summary = "가맹점 정보 수정")
    @StandardResponses
    @PutMapping("/modify")
    public ResponseEntity<MerchantUpdateResponse> updateInfo(@RequestBody @Valid MerchantUpdateRequest request) {
        return ResponseEntity.ok(merchantService.updateMyInfo(request));
    }

    @Operation(summary = "가맹점 삭제(Soft Delete)")
    @StandardResponses
    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> delete(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(merchantService.deleteMyAccount(authorizationHeader));
    }

    @Operation(summary = "가맹점 로그아웃")
    @StandardResponses
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return authService.logout(token);
    }

    @Operation(summary = "AccessToken 재발급")
    @StandardResponses
    @PostMapping("/reissue")
    public ResponseEntity<MerchantLoginResponse> reissue(@Parameter(hidden = true) @RequestHeader("Refresh-Token") String refreshToken) {
        return authService.reissue(refreshToken);
    }
}
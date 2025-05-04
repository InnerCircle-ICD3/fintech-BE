package com.fastcampus.backofficemanage.controller;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.info.MerchantInfoResponse;
import com.fastcampus.backofficemanage.dto.login.request.MerchantLoginRequest;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.dto.signup.request.MerchantSignUpRequest;
import com.fastcampus.backofficemanage.dto.signup.response.MerchantSignUpResponse;
import com.fastcampus.backofficemanage.dto.update.request.MerchantUpdateRequest;
import com.fastcampus.backofficemanage.service.AuthService;
import com.fastcampus.backofficemanage.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MerchantSignUpResponse> register(@RequestBody MerchantSignUpRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<MerchantLoginResponse> login(@RequestBody MerchantLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/info")
    public ResponseEntity<MerchantInfoResponse> getInfo() {
        return ResponseEntity.ok(merchantService.getMyInfo(getLoginId()));
    }

    @PutMapping("/modify")
    public ResponseEntity<CommonResponse> updateInfo(@RequestBody MerchantUpdateRequest request) {
        return ResponseEntity.ok(merchantService.updateMyInfo(getLoginId(), request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> delete() {
        return ResponseEntity.ok(merchantService.deleteMyAccount(getLoginId()));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(CommonResponse.builder()
                .success(true)
                .message("로그아웃 완료")
                .build());
    }

    @Operation(summary = "리프레시 토큰을 이용한 액세스 토큰 재발급")
    @SecurityRequirement(name = "refreshAuth")
    @PostMapping("/reissue")
    public ResponseEntity<MerchantLoginResponse> reissue(
            HttpServletRequest request,
            @Parameter(hidden = true) @RequestHeader(value = "Refresh-Token", required = false) String ignored) {

        String refreshToken = request.getHeader("Refresh-Token");
        String newAccessToken = authService.reissue(refreshToken);

        return ResponseEntity.ok(MerchantLoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build());
    }

    private String getLoginId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal().toString();
    }
}

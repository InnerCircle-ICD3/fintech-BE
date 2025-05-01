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
        String loginId = getLoginId();
        return ResponseEntity.ok(merchantService.getMyInfo(loginId));
    }

    @PutMapping("/modify")
    public ResponseEntity<CommonResponse> updateInfo(@RequestBody MerchantUpdateRequest request) {
        String loginId = getLoginId();
        return ResponseEntity.ok(merchantService.updateMyInfo(loginId, request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> delete() {
        String loginId = getLoginId();
        return ResponseEntity.ok(merchantService.deleteMyAccount(loginId));
    }

    private String getLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }
}

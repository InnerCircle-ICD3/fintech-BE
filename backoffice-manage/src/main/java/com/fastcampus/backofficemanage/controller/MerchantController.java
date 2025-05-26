package com.fastcampus.backofficemanage.controller;

import com.fastcampus.backofficemanage.aspect.CurrentLoginId;
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
    public ResponseEntity<MerchantInfoResponse> getInfo(@CurrentLoginId String loginId) {
        return ResponseEntity.ok(merchantService.getMyInfo(loginId));
    }

    @PutMapping("/modify")
    public ResponseEntity<CommonResponse> updateInfo(@CurrentLoginId String loginId,
                                                     @RequestBody MerchantUpdateRequest request) {
        return ResponseEntity.ok(merchantService.updateMyInfo(loginId, request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> delete(@CurrentLoginId String loginId) {
        return ResponseEntity.ok(merchantService.deleteMyAccount(loginId));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@RequestHeader("Authorization") String token) {
        return authService.logout(token);
    }

    @PostMapping("/reissue")
    public ResponseEntity<MerchantLoginResponse> reissue(@RequestHeader("Refresh-Token") String refreshToken) {
        return authService.reissue(refreshToken);
    }
}

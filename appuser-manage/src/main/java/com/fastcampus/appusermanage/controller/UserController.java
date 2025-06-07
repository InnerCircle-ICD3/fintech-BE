package com.fastcampus.appusermanage.controller;

import com.fastcampus.appusermanage.dto.CommonResponse;
import com.fastcampus.appusermanage.dto.info.UserInfoResponse;
import com.fastcampus.appusermanage.dto.login.UserLoginRequest;
import com.fastcampus.appusermanage.dto.login.UserLoginResponse;
import com.fastcampus.appusermanage.dto.signup.UserSignUpRequest;
import com.fastcampus.appusermanage.dto.signup.UserSignUpResponse;
import com.fastcampus.appusermanage.dto.update.UpdatePasswordRequest;
import com.fastcampus.appusermanage.dto.update.UserUpdateRequest;
import com.fastcampus.appusermanage.dto.update.UserUpdateResponse;
import com.fastcampus.appusermanage.service.UserService;
import com.fastcampus.common.util.SwaggerDocs.StandardResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app-users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 회원가입")
    @StandardResponses
    @PostMapping("/register")
    public ResponseEntity<UserSignUpResponse> register(@RequestBody @Valid UserSignUpRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @Operation(summary = "유저 로그인")
    @StandardResponses
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "유저 로그아웃")
    @StandardResponses
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return userService.logout(token);
    }

    @Operation(summary = "AccessToken 재발급")
    @StandardResponses
    @PostMapping("/reissue")
    public ResponseEntity<UserLoginResponse> reissue(@Parameter(hidden = true) @RequestHeader("Refresh-Token") String refreshToken) {
        return userService.reissue(refreshToken);
    }

    @Operation(summary = "내 정보 조회")
    @StandardResponses
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getMyInfo(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(userService.getMyInfoByToken(authorization));
    }

    @Operation(summary = "내 정보 수정")
    @StandardResponses
    @PutMapping("/modify")
    public ResponseEntity<UserUpdateResponse> updateMyInfo(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateMyInfo(authorization, request));
    }

    @Operation(summary = "비밀번호 변경")
    @StandardResponses
    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UpdatePasswordRequest request) {
        userService.updatePassword(authorization, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴(Soft Delete)")
    @StandardResponses
    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> deleteMyAccount(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(userService.deleteMyAccount(authorization));
    }
}

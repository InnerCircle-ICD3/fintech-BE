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
import com.fastcampus.common.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "중복된 이메일", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserSignUpResponse> register(@RequestBody @Valid UserSignUpRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @Operation(summary = "유저 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 계정", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "유저 로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 누락", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return userService.logout(token);
    }

    @Operation(summary = "AccessToken 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "RefreshToken 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reissue")
    public ResponseEntity<UserLoginResponse> reissue(@Parameter(hidden = true) @RequestHeader("Refresh-Token") String refreshToken) {
        return userService.reissue(refreshToken);
    }

    @Operation(summary = "내 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 누락", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getMyInfo(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(userService.getMyInfoByToken(authorization));
    }

    @Operation(summary = "내 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 누락", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/modify")
    public ResponseEntity<UserUpdateResponse> updateMyInfo(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateMyInfo(authorization, request));
    }

    @Operation(summary = "비밀번호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "401", description = "기존 비밀번호 불일치 또는 토큰 누락", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UpdatePasswordRequest request) {
        userService.updatePassword(authorization, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴(Soft Delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 누락", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> deleteMyAccount(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(userService.deleteMyAccount(authorization));
    }
}

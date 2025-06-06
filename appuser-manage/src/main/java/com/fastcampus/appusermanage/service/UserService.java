package com.fastcampus.appusermanage.service;

import com.fastcampus.appusermanage.dto.CommonResponse;
import com.fastcampus.appusermanage.dto.info.UserInfoResponse;
import com.fastcampus.appusermanage.dto.login.UserLoginRequest;
import com.fastcampus.appusermanage.dto.login.UserLoginResponse;
import com.fastcampus.appusermanage.dto.signup.UserSignUpRequest;
import com.fastcampus.appusermanage.dto.signup.UserSignUpResponse;
import com.fastcampus.appusermanage.dto.update.UpdatePasswordRequest;
import com.fastcampus.appusermanage.dto.update.UserUpdateRequest;
import com.fastcampus.appusermanage.dto.update.UserUpdateResponse;
import com.fastcampus.appusermanage.entity.User;
import com.fastcampus.appusermanage.jwt.JwtProvider;
import com.fastcampus.appusermanage.repository.UserRepository;
import com.fastcampus.common.constant.RedisKeys;
import com.fastcampus.common.exception.code.AuthErrorCode;
import com.fastcampus.common.exception.exception.DuplicateKeyException;
import com.fastcampus.common.exception.exception.NotFoundException;
import com.fastcampus.common.exception.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public UserSignUpResponse signup(UserSignUpRequest request) {
        validateDuplicateEmail(request.getEmail());

        String encryptedPw = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encryptedPw)
                .name(request.getName())
                .phone(request.getPhone())
                .status("ACTIVE")
                .build();

        User saved = userRepository.save(user);

        return UserSignUpResponse.builder()
                .userId(saved.getUserId())
                .email(saved.getEmail())
                .name(saved.getName())
                .status(saved.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_ID));

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new UnauthorizedException(AuthErrorCode.ACCOUNT_INACTIVE);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public ResponseEntity<CommonResponse> logout(String authorizationHeader) {
        String token = resolveBearerToken(authorizationHeader, AuthErrorCode.MISSING_ACCESS_TOKEN);
        long exp = jwtProvider.getRemainingExpiration(token);

        redisTemplate.opsForValue().set(
                RedisKeys.BLOCKLIST_PREFIX + token, "logout", exp, TimeUnit.MILLISECONDS
        );

        return ResponseEntity.ok(CommonResponse.success("로그아웃 완료"));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<UserLoginResponse> reissue(String refreshTokenHeader) {
        String refreshToken = resolveBearerToken(refreshTokenHeader, AuthErrorCode.MISSING_REFRESH_TOKEN);

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtProvider.getSubject(refreshToken);
        String newAccessToken = jwtProvider.generateAccessToken(email);

        return ResponseEntity.ok(
                UserLoginResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getMyInfoByToken(String authorizationHeader) {
        String email = extractEmailFromHeader(authorizationHeader);
        User user = findUserByEmail(email);
        return toUserInfoResponse(user);
    }

    @Transactional
    public UserUpdateResponse updateMyInfo(String authorizationHeader, UserUpdateRequest request) {
        String email = extractEmailFromHeader(authorizationHeader);
        User user = findUserByEmail(email);

        user.updateInfo(request.getName(), request.getPhone());
        user.setUpdatedAt(LocalDateTime.now(Clock.systemDefaultZone()));
        userRepository.flush();

        return toUserUpdateResponse(user);
    }

    @Transactional
    public void updatePassword(String authorizationHeader, UpdatePasswordRequest request) {
        String email = extractEmailFromHeader(authorizationHeader);
        User user = findUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now(Clock.systemDefaultZone()));
    }

    @Transactional
    public CommonResponse deleteMyAccount(String authorizationHeader) {
        String email = extractEmailFromHeader(authorizationHeader);
        User user = findUserByEmail(email);

        user.deactivate();
        user.setUpdatedAt(LocalDateTime.now(Clock.systemDefaultZone()));
        blacklistToken(authorizationHeader);

        return CommonResponse.success("회원 탈퇴가 완료되었습니다.");
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateKeyException.of(AuthErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private String extractEmailFromHeader(String header) {
        String token = resolveBearerToken(header, AuthErrorCode.MISSING_ACCESS_TOKEN);
        return jwtProvider.getSubject(token);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_ID));
    }

    private void blacklistToken(String header) {
        String token = resolveBearerToken(header, AuthErrorCode.MISSING_ACCESS_TOKEN);
        long exp = jwtProvider.getRemainingExpiration(token);
        redisTemplate.opsForValue().set(
                RedisKeys.BLOCKLIST_PREFIX + token, "logout", exp, TimeUnit.MILLISECONDS
        );
    }

    private UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .build();
    }

    private UserUpdateResponse toUserUpdateResponse(User user) {
        return new UserUpdateResponse(
                user.getName(),
                user.getPhone(),
                user.getStatus()
        );
    }

    private String resolveBearerToken(String header, AuthErrorCode missingTokenError) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException(missingTokenError);
        }
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }
}

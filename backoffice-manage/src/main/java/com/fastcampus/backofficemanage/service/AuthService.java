package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.login.request.MerchantLoginRequest;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.dto.signup.request.MerchantSignUpRequest;
import com.fastcampus.backofficemanage.dto.signup.response.MerchantSignUpResponse;
import com.fastcampus.backofficemanage.entity.Keys;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.jwt.JwtProvider;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.dto.common.CommonResponse;
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

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MerchantRepository merchantRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public MerchantSignUpResponse signup(MerchantSignUpRequest request) {
        validateDuplicateLoginId(request.getLoginId());

        String encryptedPw = passwordEncoder.encode(request.getLoginPw());

        Merchant merchant = Merchant.builder()
                .loginId(request.getLoginId())
                .loginPw(encryptedPw)
                .name(request.getName())
                .businessNumber(request.getBusinessNumber())
                .contactName(request.getContactName())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .status("ACTIVE")
                .build();

        Keys keys = Keys.createForMerchant(merchant);

        Merchant saved = merchantRepository.save(merchant);
        return MerchantSignUpResponse.builder()
                .merchantId(saved.getMerchantId())
                .loginId(saved.getLoginId())
                .name(saved.getName())
                .status(saved.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public MerchantLoginResponse login(MerchantLoginRequest request) {
        Merchant merchant = merchantRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_ID));

        if (!"ACTIVE".equals(merchant.getStatus())) {
            throw new UnauthorizedException(AuthErrorCode.ACCOUNT_INACTIVE);
        }

        if (!passwordEncoder.matches(request.getLoginPw(), merchant.getLoginPw())) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.generateAccessToken(merchant.getLoginId());
        String refreshToken = jwtProvider.generateRefreshToken(merchant.getLoginId());

        return MerchantLoginResponse.builder()
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
    public ResponseEntity<MerchantLoginResponse> reissue(String refreshTokenHeader) {
        String refreshToken = resolveBearerToken(refreshTokenHeader, AuthErrorCode.MISSING_REFRESH_TOKEN);

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String loginId = jwtProvider.getSubject(refreshToken);
        String newAccessToken = jwtProvider.generateAccessToken(loginId);

        return ResponseEntity.ok(
                MerchantLoginResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build()
        );
    }

    private void validateDuplicateLoginId(String loginId) {
        if (merchantRepository.existsByLoginId(loginId)) {
            throw DuplicateKeyException.of(AuthErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private String resolveBearerToken(String header, AuthErrorCode missingTokenError) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException(missingTokenError);
        }
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }
}

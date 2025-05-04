package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.login.request.MerchantLoginRequest;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.dto.signup.request.MerchantSignUpRequest;
import com.fastcampus.backofficemanage.dto.signup.response.MerchantSignUpResponse;
import com.fastcampus.backofficemanage.entity.Keys;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MerchantRepository merchantRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final HttpServletRequest request;

    @Transactional
    public MerchantSignUpResponse signup(MerchantSignUpRequest request) {
        if (merchantRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 로그인 ID입니다.");
        }

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

        Keys keys = Keys.builder()
                .encryptedKey(UUID.randomUUID().toString())
                .merchant(merchant)
                .build();

        merchant.setKeys(keys);

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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));

        if (!passwordEncoder.matches(request.getLoginPw(), merchant.getLoginPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(merchant.getLoginId());
        String refreshToken = jwtProvider.generateRefreshToken(merchant.getLoginId());

        return MerchantLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String token = resolveToken(request);
        long exp = jwtProvider.getRemainingExpiration(token);
        redisTemplate.opsForValue().set("BL:" + token, "logout", exp, TimeUnit.MILLISECONDS);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("Authorization header is missing or invalid");
    }

    @Transactional(readOnly = true)
    public String reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("리프레시 토큰이 없습니다.");
        }
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        String loginId = jwtProvider.getSubject(refreshToken);
        return jwtProvider.generateAccessToken(loginId);
    }
}
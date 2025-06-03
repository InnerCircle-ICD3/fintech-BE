package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.info.MerchantInfoResponse;
import com.fastcampus.backofficemanage.dto.update.request.MerchantUpdateRequest;
import com.fastcampus.backofficemanage.dto.update.response.MerchantUpdateResponse;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.jwt.JwtProvider;
import com.fastcampus.common.exception.code.AuthErrorCode;
import com.fastcampus.common.exception.code.MerchantErrorCode;
import com.fastcampus.common.exception.exception.DuplicateKeyException;
import com.fastcampus.common.exception.exception.NotFoundException;
import com.fastcampus.common.exception.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.fastcampus.common.constant.RedisKeys.BLOCKLIST_PREFIX;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Clock clock;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public MerchantInfoResponse getMyInfoByToken(String authorizationHeader) {
        String token = resolveBearerToken(authorizationHeader);
        String loginId = jwtProvider.getSubject(token);
        return getMyInfo(loginId);
    }

    @Transactional(readOnly = true)
    public MerchantInfoResponse getMyInfo(String loginId) {
        Merchant merchant = merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(MerchantErrorCode.NOT_FOUND));

        return MerchantInfoResponse.builder()
                .name(merchant.getName())
                .businessNumber(merchant.getBusinessNumber())
                .contactName(merchant.getContactName())
                .contactEmail(merchant.getContactEmail())
                .contactPhone(merchant.getContactPhone())
                .status(merchant.getStatus())
                .build();
    }

    @Transactional
    public MerchantUpdateResponse updateMyInfo(MerchantUpdateRequest request) {
        String loginId = request.getLoginId();

        Merchant merchant = merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(MerchantErrorCode.NOT_FOUND));

        if (!passwordEncoder.matches(request.getLoginPw(), merchant.getLoginPw())) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_PASSWORD);
        }

        merchant.updateInfo(
                request.getName(),
                request.getBusinessNumber(),
                request.getContactName(),
                request.getContactEmail(),
                request.getContactPhone()
        );
        merchant.setUpdatedAt(LocalDateTime.now(clock));

        try {
            merchantRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw DuplicateKeyException.of(MerchantErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        return new MerchantUpdateResponse(
                merchant.getName(),
                merchant.getBusinessNumber(),
                merchant.getContactName(),
                merchant.getContactEmail(),
                merchant.getContactPhone(),
                merchant.getStatus()
        );
    }

    @Transactional
    public CommonResponse deleteMyAccount(String authorizationHeader) {
        String token = resolveBearerToken(authorizationHeader);
        String loginId = jwtProvider.getSubject(token);

        Merchant merchant = merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(MerchantErrorCode.NOT_FOUND));

        merchant.setStatus("INACTIVE");
        merchant.setUpdatedAt(LocalDateTime.now(clock));

        // 🔒 기존 토큰을 블랙리스트 처리
        long exp = jwtProvider.getRemainingExpiration(token);
        redisTemplate.opsForValue().set(BLOCKLIST_PREFIX + token, "logout", exp, TimeUnit.MILLISECONDS);

        return CommonResponse.builder()
                .success(true)
                .message("회원 탈퇴가 완료되었습니다.")
                .build();
    }

    private String resolveBearerToken(String header) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException(AuthErrorCode.MISSING_ACCESS_TOKEN);
        }
        return header.startsWith("Bearer ")
                ? header.substring(7)
                : header;
    }
}

package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.info.MerchantInfoResponse;
import com.fastcampus.backofficemanage.dto.update.request.MerchantUpdateRequest;
import com.fastcampus.backofficemanage.dto.update.request.UpdatePasswordRequest;
import com.fastcampus.backofficemanage.dto.update.response.MerchantUpdateResponse;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.jwt.JwtProvider;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.common.constant.RedisKeys;
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
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginId(loginId);
        return toMerchantInfoResponse(merchant);
    }

    @Transactional(readOnly = true)
    public MerchantInfoResponse getMyInfo(String loginId) {
        Merchant merchant = findMerchantByLoginId(loginId);
        return toMerchantInfoResponse(merchant);
    }

    @Transactional
    public MerchantUpdateResponse updateMyInfo(String authorizationHeader, MerchantUpdateRequest request) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginId(loginId);

        merchant.updateInfo(
                request.getName(),
                request.getBusinessNumber(),
                request.getContactName(),
                request.getContactEmail(),
                request.getContactPhone()
        );
        updateTimestamp(merchant);
        flushAndHandleDuplicates();

        return toMerchantUpdateResponse(merchant);
    }

    @Transactional
    public void updatePassword(String authorizationHeader, UpdatePasswordRequest request) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginId(loginId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), merchant.getLoginPw())) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_PASSWORD);
        }

        merchant.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        updateTimestamp(merchant);
    }

    @Transactional
    public CommonResponse deleteMyAccount(String authorizationHeader) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginId(loginId);

        merchant.deactivate();
        updateTimestamp(merchant);
        blacklistToken(authorizationHeader);

        return CommonResponse.success("회원 탈퇴가 완료되었습니다.");
    }

    private String extractLoginIdFromHeader(String header) {
        String token = extractBearerToken(header);
        return jwtProvider.getSubject(token);
    }

    private Merchant findMerchantByLoginId(String loginId) {
        return merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(MerchantErrorCode.NOT_FOUND));
    }

    private void flushAndHandleDuplicates() {
        try {
            merchantRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw DuplicateKeyException.of(MerchantErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }
    }

    private void updateTimestamp(Merchant merchant) {
        merchant.setUpdatedAt(LocalDateTime.now(clock));
    }

    private void blacklistToken(String header) {
        String token = extractBearerToken(header);
        long exp = jwtProvider.getRemainingExpiration(token);
        redisTemplate.opsForValue().set(
                RedisKeys.BLOCKLIST_PREFIX + token, "logout", exp, TimeUnit.MILLISECONDS
        );
    }

    private String extractBearerToken(String header) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException(AuthErrorCode.MISSING_ACCESS_TOKEN);
        }
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }

    private MerchantInfoResponse toMerchantInfoResponse(Merchant merchant) {
        return MerchantInfoResponse.builder()
                .name(merchant.getName())
                .businessNumber(merchant.getBusinessNumber())
                .contactName(merchant.getContactName())
                .contactEmail(merchant.getContactEmail())
                .contactPhone(merchant.getContactPhone())
                .status(merchant.getStatus())
                .build();
    }

    private MerchantUpdateResponse toMerchantUpdateResponse(Merchant merchant) {
        return new MerchantUpdateResponse(
                merchant.getName(),
                merchant.getBusinessNumber(),
                merchant.getContactName(),
                merchant.getContactEmail(),
                merchant.getContactPhone(),
                merchant.getStatus()
        );
    }
}

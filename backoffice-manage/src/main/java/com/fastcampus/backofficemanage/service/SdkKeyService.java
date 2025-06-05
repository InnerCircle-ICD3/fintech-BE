package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.jwt.JwtProvider;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.common.exception.code.MerchantErrorCode;
import com.fastcampus.common.exception.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SdkKeyService {

    private final MerchantRepository merchantRepository;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public String getSdkKey(String authorizationHeader) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginId(loginId);

        if (merchant.getKeys() == null) {
            throw new NotFoundException(MerchantErrorCode.KEY_NOT_FOUND);
        }
        return merchant.getKeys().getEncryptedKey();
    }

    @Transactional
    public void deactivateSdkKey(String authorizationHeader) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginId(loginId);

        if (merchant.getKeys() == null) {
            throw new NotFoundException(MerchantErrorCode.KEY_NOT_FOUND);
        }

        merchant.getKeys().deactivate();
    }

    @Transactional
    public void activateSdkKey(String authorizationHeader) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginId(loginId);

        if (merchant.getKeys() == null) {
            throw new NotFoundException(MerchantErrorCode.KEY_NOT_FOUND);
        }

        merchant.getKeys().activate();
    }

    private String extractLoginIdFromHeader(String header) {
        String token = header.startsWith("Bearer ") ? header.substring(7) : header;
        return jwtProvider.getSubject(token);
    }

    private Merchant findMerchantByLoginId(String loginId) {
        return merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(MerchantErrorCode.NOT_FOUND));
    }
}

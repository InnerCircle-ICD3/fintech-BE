package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.entity.Keys;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.jwt.JwtProvider;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.repository.SdkKeyRepository;
import com.fastcampus.common.exception.code.MerchantErrorCode;
import com.fastcampus.common.exception.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SdkKeyService {

    private final MerchantRepository merchantRepository;
    private final SdkKeyRepository sdkKeyRepository;
    private final JwtProvider jwtProvider;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public String getSdkKey(String authorizationHeader) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginIdOrThrow(loginId);

        if (merchant.getKeys() == null) {
            throw new NotFoundException(MerchantErrorCode.KEY_NOT_FOUND);
        }
        return merchant.getKeys().getEncryptedKey();
    }

    @Transactional
    public void deactivateSdkKey(String authorizationHeader) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginIdOrThrow(loginId);

        if (merchant.getKeys() == null) {
            throw new NotFoundException(MerchantErrorCode.KEY_NOT_FOUND);
        }

        merchant.getKeys().deactivate();
    }

    @Transactional
    public void activateSdkKey(String authorizationHeader) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        Merchant merchant = findMerchantByLoginIdOrThrow(loginId);

        if (merchant.getKeys() == null) {
            throw new NotFoundException(MerchantErrorCode.KEY_NOT_FOUND);
        }

        merchant.getKeys().activate();
    }

    @Transactional(readOnly = true)
    public boolean isSdkKeyValid(String authorizationHeader) {
        Merchant merchant = findMerchantByToken(authorizationHeader);
        Keys keys = findKeysOrThrow(merchant);

        return "ACTIVE".equals(keys.getStatus()) &&
                (keys.getExpiredAt() == null || keys.getExpiredAt().isAfter(LocalDateTime.now()));
    }

    @Transactional
    public String regenerateSdkKey(String authorizationHeader) {
        Merchant merchant = findMerchantByToken(authorizationHeader);

        Keys oldKey = merchant.getKeys();
        if (oldKey != null) {
            oldKey.expire();
            sdkKeyRepository.save(oldKey);
            sdkKeyRepository.flush();
            //DB에서 merchant_id만 null로 업데이트(JPA로 없애면, key 자체를 없애는 명령어라고 착각하기때문)
            sdkKeyRepository.detachMerchant(oldKey.getKeysId());
            entityManager.clear();
        }

        // 이제 DB에 merchant_id가 null로 된 상태니까 새로운 insert 가능
        Keys newKey = Keys.createForMerchant(merchant);
        merchant.setKeys(newKey);
        sdkKeyRepository.save(newKey);

        return newKey.getEncryptedKey();
    }

    // Private
    private Merchant findMerchantByToken(String authorizationHeader) {
        String loginId = extractLoginIdFromHeader(authorizationHeader);
        return findMerchantByLoginIdOrThrow(loginId);
    }

    private String extractLoginIdFromHeader(String header) {
        String token = header.startsWith("Bearer ") ? header.substring(7) : header;
        return jwtProvider.getSubject(token);
    }

    private Merchant findMerchantByLoginIdOrThrow(String loginId) {
        return merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(MerchantErrorCode.NOT_FOUND));
    }

    private Keys findKeysOrThrow(Merchant merchant) {
        Keys keys = merchant.getKeys();
        if (keys == null) {
            throw new NotFoundException(MerchantErrorCode.KEY_NOT_FOUND);
        }
        return keys;
    }
}

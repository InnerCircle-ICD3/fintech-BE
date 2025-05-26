package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.info.MerchantInfoResponse;
import com.fastcampus.backofficemanage.dto.update.request.MerchantUpdateRequest;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.common.exception.code.MerchantErrorCode;
import com.fastcampus.common.exception.exception.DuplicateKeyException;
import com.fastcampus.common.exception.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final Clock clock;

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
    public CommonResponse updateMyInfo(String loginId, MerchantUpdateRequest request) {
        Merchant merchant = merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(MerchantErrorCode.NOT_FOUND));

        merchant.updateInfo(
                request.getName(),
                request.getBusinessNumber(),
                request.getContactName(),
                request.getContactEmail(),
                request.getContactPhone()
        );
        merchant.setUpdatedAt(LocalDateTime.now(clock));

        try {
            merchantRepository.flush(); // unique 제약조건 위반 감지용
        } catch (DataIntegrityViolationException e) {
            throw DuplicateKeyException.of(MerchantErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        return CommonResponse.builder()
                .success(true)
                .message("가맹점 정보가 성공적으로 수정되었습니다.")
                .build();
    }

    @Transactional
    public CommonResponse deleteMyAccount(String loginId) {
        Merchant merchant = merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(MerchantErrorCode.NOT_FOUND));

        merchant.setStatus("DELETED");
        merchant.setUpdatedAt(LocalDateTime.now(clock));

        return CommonResponse.builder()
                .success(true)
                .message("회원 탈퇴가 완료되었습니다.")
                .build();
    }
}

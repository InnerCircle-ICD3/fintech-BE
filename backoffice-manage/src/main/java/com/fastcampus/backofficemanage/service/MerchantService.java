package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.info.MerchantInfoResponse;
import com.fastcampus.backofficemanage.dto.update.request.MerchantUpdateRequest;
import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    @Transactional(readOnly = true)
    public MerchantInfoResponse getMyInfo(String loginId) {
        Merchant merchant = merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));

        merchant.updateInfo(
                request.getName(),
                request.getBusinessNumber(),
                request.getContactName(),
                request.getContactEmail(),
                request.getContactPhone()
        );

        return CommonResponse.builder()
                .success(true)
                .message("가맹점 정보가 성공적으로 수정되었습니다.")
                .build();
    }

    @Transactional
    public CommonResponse deleteMyAccount(String loginId) {
        Merchant merchant = merchantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));

        merchant.setStatus("DELETED");

        return CommonResponse.builder()
                .success(true)
                .message("회원 탈퇴가 완료되었습니다.")
                .build();
    }
}

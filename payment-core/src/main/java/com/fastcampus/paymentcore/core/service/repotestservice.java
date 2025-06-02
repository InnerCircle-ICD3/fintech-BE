package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentinfra.repository.KeysRepository;
import com.fastcampus.paymentinfra.entity.KeysReadOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class repotestservice {

    private final KeysRepository keysRepository;

    public Optional<KeysReadOnly> getKeyByMerchantId(Long merchantId) {
        return keysRepository.findByMerchantId(merchantId);
    }
}

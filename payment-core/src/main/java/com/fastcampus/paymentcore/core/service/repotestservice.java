package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentinfra.repository.KeysRepository;
import com.fastcampus.paymentinfra.entity.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class repotestservice {

    private final KeysRepository keysRepository;

    public Optional<Keys> getKeyByMerchantId(Long merchantId) {
        return keysRepository.findByMerchantId(merchantId);
    }
}

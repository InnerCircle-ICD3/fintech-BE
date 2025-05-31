package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.KeysReadOnly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeysRepository extends JpaRepository<KeysReadOnly, Long> {
    // merchantId 기준으로 조회
    Optional<KeysReadOnly> findByMerchantId(Long merchantId);
}

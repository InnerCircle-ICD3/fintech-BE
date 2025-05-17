package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.Keys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeysRepository extends JpaRepository<Keys, Long> {
    // merchantId 기준으로 조회
    Optional<Keys> findByMerchantId(Long merchantId);
}

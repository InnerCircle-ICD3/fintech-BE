package com.fastcampus.paymentapi.sdk.repository;

import com.fastcampus.paymentapi.sdk.entity.SdkKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SdkKeyRepository extends JpaRepository<SdkKey, Long> {
    Optional<SdkKey> findBySdkKey(String sdkKey);
}

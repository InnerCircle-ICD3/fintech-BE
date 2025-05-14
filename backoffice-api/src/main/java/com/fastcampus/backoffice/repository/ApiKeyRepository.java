package com.fastcampus.backoffice.repository;

import com.fastcampus.backoffice.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    List<ApiKey> findByMerchantId(Long merchantId);
    Optional<ApiKey> findByKey(String key);
    boolean existsByKey(String key);
} 
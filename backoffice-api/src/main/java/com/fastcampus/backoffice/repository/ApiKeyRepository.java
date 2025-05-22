package com.fastcampus.backoffice.repository;

import com.fastcampus.backoffice.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    List<ApiKey> findByMerchant_MerchantId(Long merchantId);
    Optional<ApiKey> findByEncryptedKey(String key);
    boolean existsByEncryptedKey(String key);
    @Modifying
    @Query("UPDATE ApiKey ak SET ak.active = false WHERE ak.merchant.merchantId = :merchantId AND ak.active = true")
    int deactivateActiveKeyByMerchantId(@Param("merchantId") Long merchantId);

}
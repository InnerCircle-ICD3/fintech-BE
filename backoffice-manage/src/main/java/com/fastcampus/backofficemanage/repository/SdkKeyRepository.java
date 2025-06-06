package com.fastcampus.backofficemanage.repository;

import com.fastcampus.backofficemanage.entity.Keys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SdkKeyRepository extends JpaRepository<Keys, Long> {
    @Modifying
    @Query("UPDATE Keys k SET k.merchant = null WHERE k.keysId = :keysId")
    void detachMerchant(@Param("keysId") Long keysId);
}

package com.fastcampus.backoffice.repository;

import com.fastcampus.backoffice.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByLoginId(String loginId);
    Optional<Merchant> findByBusinessNumber(String businessNumber);
    boolean existsByLoginId(String loginId);
    boolean existsByBusinessNumber(String businessNumber);
} 
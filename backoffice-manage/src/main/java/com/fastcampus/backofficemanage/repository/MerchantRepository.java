package com.fastcampus.backofficemanage.repository;

import com.fastcampus.backofficemanage.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByLoginId(String loginId);
    Optional<Merchant> findByLoginId(String loginId);
}

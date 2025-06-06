package com.fastcampus.payment.repository;

import com.fastcampus.payment.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    // 카드 토큰으로 조회
    Optional<CardInfo> findByToken(String token);

    // userId 기준으로 모든 카드 정보 조회
    @Query("SELECT c FROM CardInfo c WHERE c.userId = :userId")
    List<CardInfo> findAllByUserId(Long userId);
}

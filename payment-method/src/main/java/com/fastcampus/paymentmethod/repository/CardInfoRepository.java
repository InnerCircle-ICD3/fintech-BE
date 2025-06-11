package com.fastcampus.paymentmethod.repository;

import com.fastcampus.paymentmethod.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    /**
     * token으로 카드 찾기
     */
    Optional<CardInfo> findByToken(String token);
}
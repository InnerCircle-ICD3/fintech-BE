package com.fastcampus.paymentmethod.repository;

import com.fastcampus.paymentmethod.entity.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<UserCard, Long> {

    /**
     * token으로 카드 찾기
     */
    Optional<UserCard> findByToken(String token);
}
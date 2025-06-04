package com.fastcampus.payment.repository;

import com.fastcampus.payment.entity.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRepositoryJpa extends JpaRepository<Idempotency, Long>, ItempotencyRepository {
    Optional<Idempotency> findByIdempotencyKey(String idempotencyKey);
}
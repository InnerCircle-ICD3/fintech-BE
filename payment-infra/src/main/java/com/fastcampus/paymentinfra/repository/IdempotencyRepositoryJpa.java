package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentcore.core.entity.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepositoryJpa extends JpaRepository<Idempotency, Long> {
    Optional<Idempotency> findByIdempotencyKey(String idempotencyKey);
}
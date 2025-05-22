package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<Idempotency, Integer> {
    Optional<Idempotency> findByIdempotencyKey(String idempotencyKey);
}
package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.Idempotency;

import java.util.Optional;

public interface ItempotencyRepository {
    // TODO- 현재 구현체가 IdempotencyRepositoryJpa 뿐이지만 추후 여유가 되면 redis 구현체도 만들어 볼 예정
    public abstract Idempotency save(Idempotency idempotency);
    public abstract Optional<Idempotency> findByIdempotencyKey(String idemKey);
}

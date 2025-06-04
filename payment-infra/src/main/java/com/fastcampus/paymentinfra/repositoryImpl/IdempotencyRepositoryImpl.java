package com.fastcampus.paymentinfra.repositoryImpl;

import com.fastcampus.paymentcore.core.entity.Idempotency;
import com.fastcampus.paymentcore.core.repository.IdempotencyRepository;
import com.fastcampus.paymentinfra.repository.IdempotencyRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class IdempotencyRepositoryImpl implements IdempotencyRepository {

    @Autowired
    private final IdempotencyRepositoryJpa idempotencyRepositoryJpa;

    public IdempotencyRepositoryImpl(IdempotencyRepositoryJpa idempotencyRepositoryJpa) {
        this.idempotencyRepositoryJpa = idempotencyRepositoryJpa;
    }

    @Override
    public Idempotency save(Idempotency idempotency) {
        return idempotencyRepositoryJpa.save(idempotency);
    }

    @Override
    public Optional<Idempotency> findByIdempotencyKey(String idemKey) {
        return idempotencyRepositoryJpa.findByIdempotencyKey(idemKey);
    }
}

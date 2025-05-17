package com.fastcampus.paymentcore.core.dummy;

import com.fastcampus.paymentcore.core.dto.IdempotencyDto;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class IdempotencyRepositoryDummy {

    public Optional<IdempotencyDto> find(int idemKey) {
        return Optional.empty();
    }

    public Optional<IdempotencyDto> save(IdempotencyDto idempotencyDto) {
        return Optional.empty();
    }
}

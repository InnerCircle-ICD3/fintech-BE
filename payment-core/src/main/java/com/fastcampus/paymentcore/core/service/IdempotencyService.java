package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.dto.IdempotencyDto;
import com.fastcampus.paymentcore.core.dummy.IdempotencyRepositoryDummy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdempotencyService {

    @Autowired
    IdempotencyRepositoryDummy idempotentRepository;

    public Optional<IdempotencyDto> checkIdempotency(int idempotencyKey) {
        Optional result = idempotentRepository.find(idempotencyKey);
        return result;
    }

    public int saveIdempotency(IdempotencyDto idempotencyDto) {
        Optional<IdempotencyDto> result = idempotentRepository.save(idempotencyDto);
        return result.get().getId();
    }
}

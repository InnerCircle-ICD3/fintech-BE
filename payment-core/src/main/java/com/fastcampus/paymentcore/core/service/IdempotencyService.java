package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.dto.IdempotencyDto;
import com.fastcampus.paymentinfra.entity.Idempotency;
import com.fastcampus.paymentinfra.repository.IdempotencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;

    public Optional<IdempotencyDto> checkIdempotency(String idempotencyKey) {
        return idempotencyRepository.findByIdempotencyKey(idempotencyKey)
                .map(this::convertToDto);
    }

    public int saveIdempotency(IdempotencyDto idempotencyDto) {
        Idempotency entity = new Idempotency();
        entity.setIdempotencyKey(idempotencyDto.getIdempotencyKey());
        entity.setResponseData(idempotencyDto.getResponseData());
        return idempotencyRepository.save(entity).getId();
    }

    private IdempotencyDto convertToDto(Idempotency entity) {
        IdempotencyDto dto = new IdempotencyDto();
        dto.setId(entity.getId());
        dto.setIdempotencyKey(entity.getIdempotencyKey());
        dto.setResponseData(entity.getResponseData());
        return dto;
    }
}

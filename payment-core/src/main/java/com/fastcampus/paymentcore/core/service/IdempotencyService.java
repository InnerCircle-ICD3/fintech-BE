package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.dto.IdempotencyDto;
import com.fastcampus.paymentinfra.entity.Idempotency;
import com.fastcampus.paymentinfra.repository.ItempotencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final ItempotencyRepository idempotencyRepository;

    public Optional<IdempotencyDto> checkIdempotency(String idempotencyKey) {
        Optional<Idempotency> result = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
        // 이전에 처리한 기록이 없음
        if(result.isEmpty()) {
            return Optional.empty();
        }
        // 이미 처리한 기록이 있음
        IdempotencyDto dto = new IdempotencyDto(result.get());
        return Optional.of(dto);

    }

    public Long saveIdempotency(IdempotencyDto idempotencyDto) {
        Idempotency entity = idempotencyDto.convertToEntity();
        idempotencyRepository.save(entity);
        return entity.getId();
    }

}

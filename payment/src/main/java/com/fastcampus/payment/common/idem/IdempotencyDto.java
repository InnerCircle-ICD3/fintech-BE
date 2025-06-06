package com.fastcampus.payment.common.idem;

import com.fastcampus.payment.common.util.IdempotencyConverter;
import com.fastcampus.payment.entity.Idempotency;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
public class IdempotencyDto {
    private Long idempotencyId;
    private String idempotencyKey;
    private Object responseData;

    @Autowired
    private IdempotencyConverter converter;

    public IdempotencyDto(Long id, String idempotencyKey, Object responseData) {
        this.idempotencyId = id;
        this.idempotencyKey = idempotencyKey;
        this.responseData = responseData;
    }

    public IdempotencyDto (Idempotency entity) {
        this.idempotencyId = entity.getId();
        this.idempotencyKey = entity.getIdempotencyKey();
        this.responseData = entity.getResponseData();
    }

    public Idempotency convertToEntity() {
        Idempotency entity = new Idempotency();
        entity.setId(this.idempotencyId);
        entity.setIdempotencyKey(this.idempotencyKey);
        entity.setResponseData(converter.convertToDatabaseColumn(this.responseData));
        return entity;
    }
}

package com.fastcampus.paymentinfra.common;

public interface IdempotencyConverter {
    // 사실 core 모듈의 IdempotencyDto.convertToEntity()에서 가져다 쓰고 싶은 건 ObjectStringConverter 뿐인데 jpa 의존성 때문에 직접 참조하지 못하므로 중간에 추상화 계층을 하나 더 넣음

    public abstract String convertToDatabaseColumn(Object attribute);

    public abstract Object convertToEntityAttribute(String dbData);
}

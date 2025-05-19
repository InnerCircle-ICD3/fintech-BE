package com.fastcampus.paymentinfra.infra.repository;

import com.fastcampus.paymentinfra.infra.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 거래 정보를 Redis에 저장하고 관리하는 저장소 클래스
 * -거래 생성 시 Redis에 저장
 * -거래 조회 시 Token으로 조회
 * -거래 상태 변경 시 Redis 갱신
 * -TTL은 기본적으로 180초 설정 (추후 변경 가능)
 * Key : TX:{transactionToken}
 * value : 거래 상태 or 간단한 요약 DTO
 */


@Repository
@RequiredArgsConstructor
public class RedisTransactionRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PERFIX = "TX:";

    /**
     * 거래 정보를 Redis에 저장
     *
     * @param transactionToken 외부 식별자
     * @param value 저장할 데이터 (예: 상태 문자열, DTO등)
     * @param ttlSeconds TTL 설정 (초단위)
     */

    public void save(String transactionToken, Object value, int ttlSeconds) {
        String key = PERFIX + transactionToken;
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 거래 캐시 조회
     */
    public Optional<Object> find(String transactionToken) {
        String key = PERFIX + transactionToken;
        Object result = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(result);
    }

    /**
     * 거래 캐시 갱신(기본 TTL: 180초)
     */
    public void update(String transactionToken, Object value) {
       save(transactionToken, value, 180);
    }
    /**
     * 거래 캐시 삭제
     */
    public void delete(String transactionToken) {
        redisTemplate.delete(PERFIX + transactionToken);
    }
}

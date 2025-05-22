package com.fastcampus.paymentinfra.redis;

import com.fastcampus.paymentinfra.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 거래 정보를 Redis에 저장하고 관리하는 저장소 클래스
 * - 거래 생성 시 Redis에 저장 (TTL 포함)
 * - 거래 조회 시 Token으로 조회
 * - 거래 상태 변경 시 Redis 갱신
 * - TTL은 기본적으로 180초 설정
 */
@Repository
@RequiredArgsConstructor
public class RedisTransactionRepository {

    private final RedisTemplate<String, Transaction> redisTemplate;

    // Redis 키의 prefix: 모든 거래 객체는 TX:{token} 형식으로 저장됨
    private static final String PREFIX = "TX:";

    /**
     * Redis에서 거래 토큰을 기반으로 거래 정보 조회
     *
     * @param token 거래 식별용 토큰
     * @return Optional<Transaction> - 거래가 존재하면 객체 반환, 없으면 빈 Optional
     */
    public Optional<Transaction> findByToken(String token) {
        String key = PREFIX + token;
        Object result = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable((Transaction) result);
    }

    /**
     * 거래 정보를 Redis에 저장 (초기 생성 시 사용)
     *
     * @param tx 거래 객체
     * @param ttlSeconds Redis에 저장할 TTL (예: 180초)
     */
    public void save(Transaction tx, int ttlSeconds) {
        String key = PREFIX + tx.getTransactionToken();
        redisTemplate.opsForValue().set(key, tx, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 거래 정보를 Redis에 갱신 (TTL 유지)
     * - 상태가 바뀌었을 때 Redis도 최신 상태로 갱신
     * - TTL은 기본값 180초로 다시 설정됨
     *
     * @param tx 갱신할 거래 객체
     */
    public void update(Transaction tx) {
        save(tx, 180); // 기본 TTL 유지하면서 갱신
    }

    /**
     * Redis에서 거래 삭제 (필요 시 수동 만료 처리)
     *
     * @param token 거래 토큰
     */
    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}

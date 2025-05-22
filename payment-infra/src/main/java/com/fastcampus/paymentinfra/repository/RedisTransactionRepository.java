package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 거래 정보를 Redis에 저장하고 관리하는 저장소 클래스
 * Key : TX:{transactionToken}
 * Value : Transaction 객체 (TTL: 180초 기본)
 */
@Repository
@RequiredArgsConstructor
public class RedisTransactionRepository {

    private final RedisTemplate<String, Transaction> redisTemplate;
    private static final String PREFIX = "TX:";

    /**
     * 거래 정보를 Redis에 저장
     */
    public void save(Transaction transaction, int ttlSeconds) {
        String key = PREFIX + transaction.getTransactionToken();
        redisTemplate.opsForValue().set(key, transaction, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 거래 캐시 조회
     */
    public Optional<Transaction> findByToken(String transactionToken) {
        String key = PREFIX + transactionToken;
        Transaction result = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(result);
    }

    /**
     * 거래 정보 갱신 (TTL 180초)
     */
    public void update(Transaction transaction) {
        save(transaction, 180);
    }

    /**
     * 거래 삭제
     */
    public void delete(String transactionToken) {
        redisTemplate.delete(PREFIX + transactionToken);
    }
}

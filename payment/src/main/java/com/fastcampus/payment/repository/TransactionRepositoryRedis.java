package com.fastcampus.payment.repository;

import com.fastcampus.payment.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryRedis {

    private final RedisTemplate<String, Transaction> redisTemplate;
    private static final String PREFIX = "transaction:";

    /**
     * 주어진 토큰에 해당하는 Transaction을 Redis에서 조회합니다.
     *
     * @param token 조회할 Transaction의 토큰
     * @return Transaction이 존재하면 Optional에 담아 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    public Optional<Transaction> findByToken(String token) {
        Transaction transaction = redisTemplate.opsForValue().get(PREFIX + token);
        return Optional.ofNullable(transaction);
    }

    /**
     * Redis에 Transaction 엔티티를 지정된 TTL(초)로 저장합니다.
     *
     * @param transaction 저장할 Transaction 객체
     * @param ttlSeconds 만료까지의 시간(초)
     */
    public void save(Transaction transaction, long ttlSeconds) {
        redisTemplate.opsForValue().set(PREFIX + transaction.getTransactionToken(), transaction, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 기존 Redis에 저장된 트랜잭션의 TTL(만료 시간)을 유지하며 값을 갱신합니다.
     * TTL이 없거나 0 이하인 경우 기본 TTL 600초로 설정하여 트랜잭션을 저장합니다.
     *
     * @param transaction 갱신할 트랜잭션 객체
     */
    public void update(Transaction transaction) {
        // TTL 유지하면서 덮어쓰기
        String key = PREFIX + transaction.getTransactionToken();
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl != null && ttl > 0) {
            redisTemplate.opsForValue().set(key, transaction, ttl, TimeUnit.SECONDS);
        } else {
            // TTL 없으면 기본 600초 설정
            redisTemplate.opsForValue().set(key, transaction, 600, TimeUnit.SECONDS);
        }
    }

    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}

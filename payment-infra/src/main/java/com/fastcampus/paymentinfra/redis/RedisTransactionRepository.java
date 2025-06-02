package com.fastcampus.paymentinfra.redis;

import com.fastcampus.paymentinfra.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisTransactionRepository {

    private final RedisTemplate<String, Transaction> redisTemplate;
    private static final String PREFIX = "transaction:";

    public Optional<Transaction> findByToken(String token) {
        Transaction transaction = redisTemplate.opsForValue().get(PREFIX + token);
        return Optional.ofNullable(transaction);
    }

    public void save(Transaction transaction, long ttlSeconds) {
        redisTemplate.opsForValue().set(PREFIX + transaction.getTransactionToken(), transaction, ttlSeconds, TimeUnit.SECONDS);
    }

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

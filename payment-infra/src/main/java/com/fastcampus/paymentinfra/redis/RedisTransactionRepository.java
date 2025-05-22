package com.fastcampus.paymentinfra.redis;

import com.fastcampus.paymentinfra.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class RedisTransactionRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "transaction:";

    public Optional<Transaction> findByToken(String token) {
        Object result = redisTemplate.opsForValue().get(PREFIX + token);
        return Optional.ofNullable((Transaction) result);
    }

    public void save(Transaction transaction, long ttlSeconds) {
        redisTemplate.opsForValue().set(PREFIX + transaction.getTransactionToken(), transaction, ttlSeconds, TimeUnit.SECONDS);
    }

    public void update(Transaction transaction) {
        String key = PREFIX + transaction.getTransactionToken();
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl != null && ttl > 0) {
            redisTemplate.opsForValue().set(key, transaction, ttl, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, transaction, 600, TimeUnit.SECONDS);
        }
    }

    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}

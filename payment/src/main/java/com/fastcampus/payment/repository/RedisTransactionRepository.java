package com.fastcampus.payment.repository;

import com.fastcampus.payment.entity.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RedisTransactionRepository {

    private final RedisTemplate<String, Transaction> redisTemplate;
    private static final String PREFIX = "TX:";

    /**
     * 지정된 TTL(초)로 거래 정보를 Redis에 저장합니다.
     *
     * @param transaction 저장할 거래 정보
     * @param ttlSeconds 거래 정보의 만료 시간(초)
     */
    public void save(Transaction transaction, int ttlSeconds, String token) {
        String key = PREFIX + token;
        redisTemplate.opsForValue().set(key, transaction, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 주어진 거래 토큰에 해당하는 거래 정보를 Redis에서 조회하여 반환합니다.
     *
     * @param transactionToken 조회할 거래의 토큰
     * @return 거래 정보가 존재하면 Optional에 담아 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    public Optional<Transaction> findByToken(String transactionToken) {
        String key = PREFIX + transactionToken;
        Transaction result = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(result);
    }

    /****
     * 주어진 거래 정보를 Redis에 180초의 TTL로 갱신합니다.
     *
     * @param transaction 갱신할 거래 정보
     */
    public void update(Transaction transaction, String token) {
        save(transaction, 180, token);
    }

    /****
     * 주어진 거래 토큰에 해당하는 Redis의 거래 데이터를 삭제합니다.
     *
     * @param transactionToken 삭제할 거래의 토큰
     */
    public void delete(String transactionToken) {
        redisTemplate.delete(PREFIX + transactionToken);
    }
}

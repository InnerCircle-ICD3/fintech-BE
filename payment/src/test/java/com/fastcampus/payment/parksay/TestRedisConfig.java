package com.fastcampus.payment.parksay;

import com.fastcampus.payment.entity.Transaction;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@TestConfiguration
public class TestRedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 간단히 Embedded Redis 사용하거나, Mockito로 대체 가능
        // 여기선 단순 mock 예시
        return Mockito.mock(RedisConnectionFactory.class);
    }

    @Bean
    public RedisTemplate<String, Transaction> transactionRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Transaction> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}

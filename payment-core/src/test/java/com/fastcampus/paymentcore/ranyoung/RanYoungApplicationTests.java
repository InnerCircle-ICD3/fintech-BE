package com.fastcampus.paymentcore.ranyoung;

import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.entity.TransactionStatus;
import com.fastcampus.paymentinfra.redis.RedisTransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmbeddedRedisConfig.class)
public class RanYoungApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RedisTemplate<String, Transaction> redisTemplate;

	private RedisTransactionRepository redisTransactionRepository;

	/**
	 * 테스트 실행 전에 RedisTransactionRepository를 직접 생성해서 주입
	 */
	@BeforeEach
	void setup() {
		this.redisTransactionRepository = new RedisTransactionRepository(redisTemplate);
	}

	/**
	 * Redis 연결 및 객체 저장/조회 테스트
	 */
	@Test
	void redis에_거래_저장하고_조회된다() {
		// given
		Transaction tx = new Transaction();
		tx.setTransactionToken("test123");
		tx.setMerchantId(1L);
		tx.setMerchantOrderId("ORD001");
		tx.setAmount(10000L);
		tx.setStatus(TransactionStatus.REQUESTED);
		tx.setExpireAt(LocalDateTime.now().plusMinutes(3));

		// when
		redisTransactionRepository.save(tx, 180);

		// then
		Transaction saved = redisTransactionRepository.findByToken("test123")
				.orElseThrow();
		assertThat(saved.getAmount()).isEqualTo(10000L);
		assertThat(saved.getStatus()).isEqualTo("READY");
	}
}

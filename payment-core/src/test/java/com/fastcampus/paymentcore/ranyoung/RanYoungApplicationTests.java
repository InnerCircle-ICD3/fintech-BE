package com.fastcampus.paymentcore.ranyoung;

import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.redis.RedisTransactionRepository;
import com.fastcampus.paymentinfra.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmbeddedRedisConfig.class)
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "com.fastcampus.paymentinfra.repository")
@EnableRedisRepositories(basePackages = "com.fastcampus.paymentinfra.redis")
@EntityScan(basePackages = "com.fastcampus.paymentinfra.entity")
public class RanYoungApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RedisTemplate<String, Transaction> redisTemplate;


	private TransactionRepository transactionRepository;

	private RedisTransactionRepository redisTransactionRepository;

	@BeforeEach
	void setup() {
		this.redisTransactionRepository = new RedisTransactionRepository(redisTemplate);
	}

	@Test
	void redis에_거래_저장하고_조회된다() {
		// given
		Transaction tx = new Transaction();
		tx.setTransactionToken("test123");
		tx.setMerchantId(1L);
		tx.setMerchantOrderId("ORD001");
		tx.setAmount(10000L);
		tx.setStatus("READY");
		tx.setExpireAt(LocalDateTime.now().plusMinutes(3));

		// when
		redisTransactionRepository.save(tx, 180);

		// then
		Transaction saved = redisTransactionRepository.findByToken("test123")
				.orElseThrow();
		assertThat(saved.getAmount()).isEqualTo(10000L);
		assertThat(saved.getStatus()).isEqualTo("READY");
	}

	@Test
	void postgres에_거래_저장하고_조회된다() {
		// given
		Transaction tx = new Transaction();
		tx.setTransactionToken("pg123");
		tx.setMerchantId(2L);
		tx.setMerchantOrderId("ORD-PG-001");
		tx.setAmount(20000L);
		tx.setStatus("READY");
		tx.setExpireAt(LocalDateTime.now().plusMinutes(5));

		// when
		transactionRepository.save(tx);

		// then
		Transaction saved = transactionRepository.findByTransactionToken("pg123")
				.orElseThrow();
		assertThat(saved.getAmount()).isEqualTo(20000L);
		assertThat(saved.getStatus()).isEqualTo("READY");
	}


	@Autowired
	private org.springframework.context.ApplicationContext applicationContext;

	@Test
	void springBean목록출력() {
		String[] beans = applicationContext.getBeanDefinitionNames();
		for (String bean : beans) {
			if (bean.toLowerCase().contains("transactionrepository")) {
				System.out.println("✅ 등록된 Bean: " + bean);
			}
		}
	}
}

package com.fastcampus.payment;

import com.fastcampus.payment.common.exception.BadRequestException;
import com.fastcampus.payment.dto.PaymentExecutionRequest;
import com.fastcampus.payment.dto.PaymentProgressResponse;
import com.fastcampus.payment.entity.CardInfo;
import com.fastcampus.payment.entity.PaymentMethod;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.entity.TransactionStatus;
import com.fastcampus.payment.repository.CardInfoRepository;
import com.fastcampus.payment.repository.PaymentMethodRepository;
import com.fastcampus.payment.repository.TransactionRepository;
import com.fastcampus.payment.repository.TransactionRepositoryRedis;
import com.fastcampus.payment.service.PaymentExecutionService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        // 🔥 Redis 자동 설정을 완전히 끄고, Redis 관련 Bean들도 스캔 제외
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
        "spring.main.allow-bean-definition-overriding=true"
})
class PaymentExecutionServiceImplTest {

    // 🔥 가장 간단한 방법: TestConfiguration으로 모든 Redis 관련 Bean을 Mock으로 생성
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public TransactionRepositoryRedis transactionRepositoryRedis() {
            return mock(TransactionRepositoryRedis.class);
        }

        @Bean
        @Primary
        public Object redisTransactionRepository() {
            return mock(Object.class); // RedisTransactionRepository 클래스가 뭔지 모르니 Object로 Mock
        }

        @Bean("redisTemplate")
        @Primary
        public org.springframework.data.redis.core.RedisTemplate<String, Transaction> redisTemplate() {
            return mock(org.springframework.data.redis.core.RedisTemplate.class);
        }
    }

    @Autowired
    private PaymentExecutionService paymentExecutionService;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @Transactional
    @DisplayName("존재하지 않는 카드 토큰 - 예외 발생")
    void executePayment_CardNotFound_ThrowsException() {
        // Given: 거래는 생성하되, 카드는 생성하지 않음
        Transaction transaction = createTestTransaction("test_transaction_456");
        transactionRepository.save(transaction);

        PaymentMethod paymentMethod = createTestPaymentMethod("CARD");
        paymentMethodRepository.save(paymentMethod);

        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setTransactionToken("test_transaction_456");
        request.setCardToken("invalid_card_token");  // 🚨 존재하지 않는 토큰
        request.setPaymentMethodType("CARD");

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> paymentExecutionService.execute(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("카드 정보를 찾을 수 없습니다");
    }

    @Test
    @Transactional
    @DisplayName("결제 실행 성공 - 통합 테스트")
    void executePayment_Success() {
        // Given: 모든 필요한 데이터 생성
        Transaction transaction = createTestTransaction("test_transaction_789");
        transactionRepository.save(transaction);

        CardInfo cardInfo = createTestCardInfo("valid_card_token", "VISA");
        cardInfoRepository.save(cardInfo);

        PaymentMethod paymentMethod = createTestPaymentMethod("CARD");
        paymentMethodRepository.save(paymentMethod);

        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setTransactionToken("test_transaction_789");
        request.setCardToken("valid_card_token");
        request.setPaymentMethodType("CARD");

        // When: 결제 실행
        PaymentProgressResponse response = paymentExecutionService.execute(request);

        // Then: 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.getTransactionToken()).isEqualTo("test_transaction_789");
        // 🔥 수정: getStatus()가 String을 반환하므로 String으로 비교
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getAmount()).isEqualTo(10000L);
    }

    // 🔧 테스트 헬퍼 메서드들
    private CardInfo createTestCardInfo(String token, String company) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setToken(token);
        cardInfo.setUserId(1L);
        cardInfo.setCardCompany(company);
        cardInfo.setLast4("1234");
        cardInfo.setType("CREDIT");
        return cardInfo;
    }

    private PaymentMethod createTestPaymentMethod(String type) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(type);
        paymentMethod.setIsActive(true);
        return paymentMethod;
    }

    private Transaction createTestTransaction(String token) {
        Transaction transaction = new Transaction();
        transaction.setTransactionToken(token);
        transaction.setMerchantId(1L);
        transaction.setMerchantOrderId("ORDER_123");
        transaction.setAmount(10000L);
        transaction.setStatus(TransactionStatus.READY);
        return transaction;
    }
}
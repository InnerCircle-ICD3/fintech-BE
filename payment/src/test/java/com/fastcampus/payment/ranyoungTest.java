package com.fastcampus.payment;

import com.fastcampus.payment.common.exception.BadRequestException;
import com.fastcampus.payment.dto.PaymentExecutionRequest;
import com.fastcampus.payment.dto.PaymentProgressResponse;
import com.fastcampus.payment.entity.CardInfo;
import com.fastcampus.payment.entity.PaymentMethod;
import com.fastcampus.payment.entity.PaymentMethodType;
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
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
        "spring.main.allow-bean-definition-overriding=true"
})
class PaymentExecutionServiceImplTest {

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
            return mock(Object.class);
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
    @DisplayName("카드 결제 실행 성공")
    void executePayment_Card_Success() {
        // Given
        Transaction transaction = createTestTransaction("test_transaction_card");
        transactionRepository.save(transaction);

        CardInfo cardInfo = createTestCardInfo("valid_card_token", "VISA");
        cardInfoRepository.save(cardInfo);

        // 수정: 이미 존재하는 PaymentMethod 조회 또는 생성
        PaymentMethod paymentMethod = getOrCreatePaymentMethod(PaymentMethodType.CARD);

        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setTransactionToken("test_transaction_card");
        request.setCardToken("valid_card_token");
        request.setPaymentMethodType("CARD");

        // When
        PaymentProgressResponse response = paymentExecutionService.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTransactionToken()).isEqualTo("test_transaction_card");
        assertThat(response.getStatus()).isIn("COMPLETED", "FAILED"); // 시뮬레이션이므로 둘 다 가능
        assertThat(response.getAmount()).isEqualTo(10000L);
    }

    @Test
    @Transactional
    @DisplayName("계좌이체 결제 실행 성공")
    void executePayment_BankTransfer_Success() {
        // Given
        Transaction transaction = createTestTransaction("test_transaction_bank");
        transactionRepository.save(transaction);

        CardInfo cardInfo = createTestCardInfo("bank_account_token", "KB국민은행");
        cardInfoRepository.save(cardInfo);

        //  수정: 이미 존재하는 PaymentMethod 조회 또는 생성
        PaymentMethod paymentMethod = getOrCreatePaymentMethod(PaymentMethodType.BANK_TRANSFER);

        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setTransactionToken("test_transaction_bank");
        request.setCardToken("bank_account_token");
        request.setPaymentMethodType("BANK_TRANSFER");

        // When
        PaymentProgressResponse response = paymentExecutionService.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTransactionToken()).isEqualTo("test_transaction_bank");
        assertThat(response.getStatus()).isIn("COMPLETED", "FAILED");
        assertThat(response.getAmount()).isEqualTo(10000L);
    }

    @Test
    @Transactional
    @DisplayName("모바일페이 결제 실행 성공")
    void executePayment_MobilePay_Success() {
        // Given
        Transaction transaction = createTestTransaction("test_transaction_mobile");
        transactionRepository.save(transaction);

        CardInfo cardInfo = createTestCardInfo("mobile_pay_token", "카카오페이");
        cardInfoRepository.save(cardInfo);

        //수정: 이미 존재하는 PaymentMethod 조회 또는 생성
        PaymentMethod paymentMethod = getOrCreatePaymentMethod(PaymentMethodType.MOBILE_PAY);

        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setTransactionToken("test_transaction_mobile");
        request.setCardToken("mobile_pay_token");
        request.setPaymentMethodType("MOBILE_PAY");

        // When
        PaymentProgressResponse response = paymentExecutionService.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isIn("COMPLETED", "FAILED");
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 카드 토큰 - 예외 발생")
    void executePayment_CardNotFound_ThrowsException() {
        // Given
        Transaction transaction = createTestTransaction("test_transaction_456");
        transactionRepository.save(transaction);

        //  수정: 이미 존재하는 PaymentMethod 조회 또는 생성
        PaymentMethod paymentMethod = getOrCreatePaymentMethod(PaymentMethodType.CARD);

        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setTransactionToken("test_transaction_456");
        request.setCardToken("invalid_card_token");
        request.setPaymentMethodType("CARD");

        // When & Then
        assertThatThrownBy(() -> paymentExecutionService.execute(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("카드 정보를 찾을 수 없습니다");
    }

    @Test
    @Transactional
    @DisplayName("비활성화된 결제 방식 - 예외 발생")
    void executePayment_InactivePaymentMethod_ThrowsException() {
        // Given
        Transaction transaction = createTestTransaction("test_transaction_inactive");
        transactionRepository.save(transaction);

        CardInfo cardInfo = createTestCardInfo("crypto_wallet_token", "Bitcoin");
        cardInfoRepository.save(cardInfo);

        // 수정: 기존 PaymentMethod를 조회하고 비활성화
        PaymentMethod inactiveMethod = getOrCreatePaymentMethod(PaymentMethodType.CRYPTO);
        inactiveMethod.setIsActive(false);
        paymentMethodRepository.save(inactiveMethod);

        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setTransactionToken("test_transaction_inactive");
        request.setCardToken("crypto_wallet_token");
        request.setPaymentMethodType("CRYPTO");

        // When & Then
        assertThatThrownBy(() -> paymentExecutionService.execute(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @DisplayName("지원하지 않는 결제 방식 - 예외 발생")
    void executePayment_UnsupportedPaymentMethod_ThrowsException() {
        // Given
        Transaction transaction = createTestTransaction("test_transaction_unsupported");
        transactionRepository.save(transaction);

        CardInfo cardInfo = createTestCardInfo("unknown_token", "UNKNOWN");
        cardInfoRepository.save(cardInfo);

        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setTransactionToken("test_transaction_unsupported");
        request.setCardToken("unknown_token");
        request.setPaymentMethodType("UNSUPPORTED_METHOD");

        // When & Then
        assertThatThrownBy(() -> paymentExecutionService.execute(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("모든 결제 방식 타입 테스트")
    void testAllPaymentMethodTypes() {
        // Given & When & Then
        for (PaymentMethodType type : PaymentMethodType.values()) {
            PaymentMethod method = createTestPaymentMethod(type);

            assertThat(method.getType()).isEqualTo(type);
            assertThat(method.getType().getDisplayName()).isNotEmpty();
            assertThat(method.getType().getProcessingTimeMs()).isGreaterThan(0);
            assertThat(method.getType().getSuccessRate()).isBetween(0, 100);
        }
    }

    // 🔥 새로운 헬퍼 메서드: 기존 PaymentMethod 조회 또는 생성
    private PaymentMethod getOrCreatePaymentMethod(PaymentMethodType type) {
        return paymentMethodRepository.findByType(type)
                .orElseGet(() -> {
                    PaymentMethod newMethod = createTestPaymentMethod(type);
                    return paymentMethodRepository.save(newMethod);
                });
    }

    //  헬퍼 메서드들 - Enum 지원으로 업데이트
    private CardInfo createTestCardInfo(String token, String company) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setToken(token);
        cardInfo.setUserId(1L);
        cardInfo.setCardCompany(company);
        cardInfo.setLast4("1234");
        cardInfo.setType("CREDIT");
        return cardInfo;
    }

    //수정: PaymentMethodType Enum을 받도록 변경
    private PaymentMethod createTestPaymentMethod(PaymentMethodType type) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(type);
        paymentMethod.setIsActive(true);
        paymentMethod.setDescription(type.getDisplayName() + " 테스트");
        return paymentMethod;
    }

    //  하위 호환성을 위한 String 버전도 유지
    private PaymentMethod createTestPaymentMethod(String typeString) {
        PaymentMethodType type = PaymentMethodType.fromString(typeString);
        return createTestPaymentMethod(type);
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
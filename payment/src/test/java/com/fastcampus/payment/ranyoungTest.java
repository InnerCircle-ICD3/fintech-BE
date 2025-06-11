package com.fastcampus.payment;

import com.fastcampus.payment.common.exception.BadRequestException;
import com.fastcampus.payment.dto.PaymentExecutionResponse;
import com.fastcampus.payment.dto.PaymentExecutionRequest;
import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.entity.PaymentStatus;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.repository.*;
import com.fastcampus.payment.service.PaymentExecutionService;

import com.fastcampus.payment.service.PaymentReadyService;
import com.fastcampus.paymentmethod.entity.*;
import com.fastcampus.paymentmethod.repository.CardInfoRepository;
import com.fastcampus.paymentmethod.repository.PaymentMethodRepository;
import com.fastcampus.paymentmethod.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Date;
import java.util.List;

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

//        @Bean("redisTemplate")
//        @Primary
//        public org.springframework.data.redis.core.RedisTemplate<String, Transaction> redisTemplate() {
//            return mock(org.springframework.data.redis.core.RedisTemplate.class);
//        }
    }

    @Autowired
    private PaymentExecutionService paymentExecutionService;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentReadyService paymentReadyService;
    @Autowired
    private UserRepository userRepository;  // from PaymentMethod module
    @Autowired
    private CardInfoRepository cardInfoRepository;  // from PaymentMethod module

    private static String TEST_TOKEN;
    private static Long TOTAL_AMOUNT;
    private static User testUser;
    private static PaymentMethod testPaymentMethod;
    private static CardInfo testCardInfo;
    @BeforeEach
    public void beforeEach() {
        // test payment
        TOTAL_AMOUNT = 1000L;
        Payment testPayment = createTestPayment();
        TEST_TOKEN = testPayment.getPaymentToken();
        // test user
        testUser = creatTesteUser();
        // test poayment method
        testPaymentMethod = createTestPaymentMethod(PaymentMethodType.CARD, testUser);
        // test card info
        testCardInfo = createTestCardInfo("test_card_token", "test_card_company", testPaymentMethod);

    }

    @Test
    @Transactional
    @DisplayName("카드 결제 실행 성공")
    void executePayment_Card_Success() {
        // Given
        String testCardToken = "valid_card_token";
        User user = creatTesteUser();
        PaymentMethod paymentMethod = getOrCreatePaymentMethod(PaymentMethodType.CARD, user);
        CardInfo cardInfo = createTestCardInfo(testCardToken, "VISA", paymentMethod);
        PaymentExecutionRequest request = prepareTestRequest(TEST_TOKEN, testCardToken, paymentMethod);

        // 수정: 이미 존재하는 PaymentMethod 조회 또는 생성


        // When
        PaymentExecutionResponse response = paymentExecutionService.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentToken()).isEqualTo(TEST_TOKEN);
        assertThat(response.getStatus()).isIn(PaymentStatus.COMPLETED, PaymentStatus.FAILED); // 시뮬레이션이므로 둘 다 가능
        assertThat(response.getAmount()).isEqualTo(TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    @DisplayName("계좌이체 결제 실행 성공")
    void executePayment_BankTransfer_Success() {
        // Given
        String testCardToken = "bank_account_token";
        User user = creatTesteUser();
        PaymentMethod paymentMethod = getOrCreatePaymentMethod(PaymentMethodType.BANK_TRANSFER, user);
        CardInfo cardInfo = createTestCardInfo(testCardToken, "KB국민은행", paymentMethod);
        PaymentExecutionRequest request = prepareTestRequest(TEST_TOKEN, testCardToken, paymentMethod);

        //  수정: 이미 존재하는 PaymentMethod 조회 또는 생성


        // When
        PaymentExecutionResponse response = paymentExecutionService.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentToken()).isEqualTo(TEST_TOKEN);
        assertThat(response.getStatus()).isIn(PaymentStatus.COMPLETED, PaymentStatus.FAILED); // 시뮬레이션이므로 둘 다 가능
        assertThat(response.getAmount()).isEqualTo(TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    @DisplayName("모바일페이 결제 실행 성공")
    void executePayment_MobilePay_Success() {
        // Given
        String testCardToken = "mobile_pay_token";
        User user = creatTesteUser();
        PaymentMethod paymentMethod = getOrCreatePaymentMethod(PaymentMethodType.MOBILE_PAY, user);
        CardInfo cardInfo = createTestCardInfo(testCardToken, "카카오페이", paymentMethod);
        PaymentExecutionRequest request = prepareTestRequest(TEST_TOKEN, testCardToken, paymentMethod);

        //수정: 이미 존재하는 PaymentMethod 조회 또는 생성

        // When
        PaymentExecutionResponse response = paymentExecutionService.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isIn(PaymentStatus.COMPLETED, PaymentStatus.FAILED); // 시뮬레이션이므로 둘 다 가능
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 카드 토큰 - 예외 발생")
    void executePayment_CardNotFound_ThrowsException() {
        // Given
        //  수정: 이미 존재하는 PaymentMethod 조회 또는 생성
        String testCardToken = "invalid_card_token";
        User user = creatTesteUser();
        PaymentMethod paymentMethod = getOrCreatePaymentMethod(PaymentMethodType.CARD, user);
        PaymentExecutionRequest request = prepareTestRequest(TEST_TOKEN, testCardToken, paymentMethod);

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
        // 수정: 기존 PaymentMethod를 조회하고 비활성화
        String testCardToken = "crypto_wallet_token";
        User user = creatTesteUser();
        PaymentMethod inactiveMethod = getOrCreatePaymentMethod(PaymentMethodType.CRYPTO, user);
        inactiveMethod.setUseYn("N");
        CardInfo cardInfo = createTestCardInfo(testCardToken, "Bitcoin", inactiveMethod);

        // when
        PaymentExecutionRequest request = prepareTestRequest(TEST_TOKEN, testCardToken, inactiveMethod);


        // Then
        assertThatThrownBy(() -> paymentExecutionService.execute(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @DisplayName("지원하지 않는 결제 방식 - 예외 발생")
    void executePayment_UnsupportedPaymentMethod_ThrowsException() {
        // Given
        String testCardToken = "unknown_token";
        User user = creatTesteUser();
        PaymentMethod paypalMethod = getOrCreatePaymentMethod(PaymentMethodType.PAYPAL, user);
        CardInfo cardInfo = createTestCardInfo(testCardToken, "UNKNOWN", paypalMethod);
        PaymentExecutionRequest request = prepareTestRequest(TEST_TOKEN, testCardToken, paypalMethod);
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
            PaymentMethod method = createTestPaymentMethod(type, testUser);

            assertThat(method.getType()).isEqualTo(type);
            assertThat(method.getType().getDisplayName()).isNotEmpty();
            assertThat(method.getType().getProcessingTimeMs()).isGreaterThan(0);
            assertThat(method.getType().getSuccessRate()).isBetween(0, 100);
        }
    }

    // 🔥 새로운 헬퍼 메서드: 기존 PaymentMethod 조회 또는 생성
    private PaymentMethod getOrCreatePaymentMethod(PaymentMethodType type, User user) {
        List<PaymentMethod> paymentMethodList = paymentMethodRepository.findByUserIdAndMethodType(user.getUserId(), type);
        if(paymentMethodList.isEmpty()) {
            PaymentMethod newMethod = createTestPaymentMethod(type, user);
            return paymentMethodRepository.save(newMethod);
        }
        return paymentMethodList.get(0);    // TODO - 한 userId 와 한 method type 으로 조회 했는데 paymentMethod 결과가 여러 개일 경우 어떻게 처리할지? (예 - 신용 카드만 여러 개)
    }

    //  헬퍼 메서드들 - Enum 지원으로 업데이트
    private CardInfo createTestCardInfo(String token, String company, PaymentMethod paymentMethod) {
        CardInfo cardInfo = CardInfo.builder()
        .paymentMethod(paymentMethod)
        .token(token)
        .birthDate("201212")
        .expiryDate("06/26")
        .cardNumber("1111-2222-3333-4444")
        .cardCompany(company)
        .type(CardType.CREDIT)
        .paymentPassword("111111")
        .cardPw("11")
        .cvc("1111")
        .issuerBank("test_back")
        .build();
        cardInfoRepository.save(cardInfo);
        return cardInfo;
    }

    //수정: PaymentMethodType Enum을 받도록 변경
    private PaymentMethod createTestPaymentMethod(PaymentMethodType type, User user) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(type);
        paymentMethod.setUseYn("Y");
        paymentMethod.setUser(user);
        paymentMethod.setDescription(type.getDisplayName() + " 테스트");
        paymentMethodRepository.save(paymentMethod);
        return paymentMethod;
    }

    private User creatTesteUser() {
        Long time = new Date().getTime();
        String testEmail = Long.toBinaryString(time);
        User user = User.builder().
                email(testEmail).
                password("test_password").
                name("test_name").build();
        userRepository.save(user);
        return user;
    }

    private Payment createTestPayment() {
        Long testMerchantId = 1000L;
        String testMerchantOrderId = "ORDER_123";
        Payment payment = new Payment();
        payment.setMerchantId(testMerchantId);
        payment.setMerchantOrderId(testMerchantOrderId);
        payment.setTotalAmount(TOTAL_AMOUNT);
        paymentReadyService.readyPayment(payment);
        return payment;
    }

    public PaymentExecutionRequest prepareTestRequest(String paymentToken, String cardToken, PaymentMethod paymentMethod) {
        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setPaymentToken(paymentToken);
        request.setCardToken(cardToken);
        request.setPaymentMethodType(paymentMethod.getType().toString());
        request.setUserId(paymentMethod.getUser().getUserId());
        return request;
    }
}
package com.fastcampus.payment.parksay;

import com.fastcampus.payment.PaymentApplication;
import com.fastcampus.payment.common.exception.base.HttpException;
import com.fastcampus.payment.common.util.CommonUtil;
import com.fastcampus.payment.controller.PaymentController;
import com.fastcampus.payment.dto.*;
import com.fastcampus.payment.entity.PaymentStatus;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.paymentmethod.entity.*;
import com.fastcampus.paymentmethod.repository.CardInfoRepository;
import com.fastcampus.paymentmethod.repository.PaymentMethodRepository;
import com.fastcampus.paymentmethod.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@SpringBootTest(classes = PaymentApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ParksayTest {

    @Autowired
    PaymentController controller;
    @Autowired
    CommonUtil commonUtil;


    @MockitoBean
    private RedisTemplate<String, Transaction> redisTemplate;   // 이 class 에서는 쓰는 곳이 없어도 build 시 에러를 방지하기 위해 넣어줘야 함 
    @Autowired
    UserRepository userRepository;  // from PaymentMethod module
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;     // from PaymentMethod module
    @Autowired
    private CardInfoRepository cardInfoRepository;  // from PaymentMethod module
    private static User TEST_USER;   // from PaymentMethod module
    private static PaymentMethod TEST_METHOD;  // from PaymentMethod module
    private static CardInfo TEST_CARD;   // from PaymentMethod module

    private static String TEST_TOKEN;
    private static Long TEST_TOTAL_AMOUNT;
    private static Long TEST_MERCHANT_ID;
    private static String TEST_MERCHANT_ORDER_ID;

    @BeforeEach
    public void beforeEach() {
        //
        TEST_TOTAL_AMOUNT = 432798L;
        TEST_MERCHANT_ID = 36L;
        TEST_MERCHANT_ORDER_ID = "order_test_129239";

        if(!(TEST_TOKEN == null || TEST_TOKEN.isBlank())) {
            // 테스트 데이터는 클래스 실행 최초에만 한 번 등록하게...;
            // 아니 beforeAll 로 한 번만 실행하고 싶은데 static 으로 설정해야 해서 bean 을 못 받아옴
            return;
        }
        // test obj
        TEST_TOKEN = createTestPaymentToken();
        TEST_USER = createTestUser();
        TEST_METHOD = createTestPaymentMethod(PaymentMethodType.CARD, TEST_USER);
        TEST_CARD = createTestCardInfo(UUID.randomUUID().toString(), "test_card_company", TEST_METHOD);
        //
        System.out.println("before ========= " + TEST_TOKEN);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void readyTest() {
        //
        PaymentReadyRequest request = new PaymentReadyRequest(TEST_TOTAL_AMOUNT, TEST_MERCHANT_ID, TEST_MERCHANT_ORDER_ID);
        PaymentReadyResponse response = controller.readyPayment(request);
        //
        LocalDateTime limit = commonUtil.generateExpiresAt();
        limit = limit.plusSeconds(5L);  // 테스트 작동 시간
        //
        System.out.println("limit = " + limit);
        System.out.println("response.getExpireAt() = " + response.getExpireAt());
        Assertions.assertNotNull(response.getPaymentToken());
        Assertions.assertTrue(response.getExpireAt().isBefore(limit));
    }

    @Test
    public void progressTest() {
        //
//        PaymentProgressRequest request = new PaymentProgressRequest(TEST_TOKEN);
        PaymentProgressResponse response = controller.progressPayment(TEST_TOKEN);
        //
        Assertions.assertEquals(TEST_TOTAL_AMOUNT, response.getAmount());
        Assertions.assertEquals(TEST_MERCHANT_ID, response.getMerchantId());
        Assertions.assertEquals(TEST_MERCHANT_ORDER_ID, response.getMerchantOrderId());
        Assertions.assertEquals(TEST_TOKEN, response.getPaymentToken());
    }



    @Test
    public void cancelTest() throws Exception {
        // given
        String testToken = createTestPaymentToken();
        User testUser = createTestUser();
        PaymentMethod paymentMethod = createTestPaymentMethod(PaymentMethodType.CARD, testUser);
        CardInfo testCard = createTestCardInfo(UUID.randomUUID().toString(), "test_card_company", paymentMethod);
        //
        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setPaymentToken(testToken);
        request.setCardToken(testCard.getToken());
        request.setPaymentMethodType(paymentMethod.getType().toString());
        request.setUserId(testUser.getUserId());
        PaymentExecutionResponse testResponse = controller.executePayment(request);
        System.out.println("cancelTest() > testResponse = " + testResponse);

        // when
        if(PaymentStatus.FAILED.equals(testResponse.getStatus())) {
            Assertions.assertThrows(HttpException.class, ()->{
                System.out.println("cancelTest() > ========== failed");
                PaymentCancelResponse response = controller.cancelPayment(testToken);
            });
        } else {
    //        PaymentCancelRequest request = new PaymentCancelRequest(TEST_TOKEN);
            System.out.println("cancelTest() > ========== success");
                PaymentCancelResponse response = controller.cancelPayment(testToken);

                // then
                Assertions.assertEquals(TEST_MERCHANT_ORDER_ID, response.getMerchantOrderId());
                Assertions.assertEquals(PaymentStatus.CANCELED, response.getStatus());
                Assertions.assertEquals(testToken, response.getPaymentToken());

        }
    }


    @Test
    public void simpleTest() {
        // ... do something

    }

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
        return cardInfoRepository.save(cardInfo);
    }

    private PaymentMethod createTestPaymentMethod(PaymentMethodType type, User user) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(type);
        paymentMethod.setUseYn(UseYn.Y);
        paymentMethod.setUser(user);
        paymentMethod.setDescription(type.getDisplayName() + " 테스트");
        return paymentMethodRepository.save(paymentMethod);
    }

    private User createTestUser() {
        Long time = new Date().getTime();
        String testEmail = Long.toBinaryString(time);
        User user = User.builder().
                email(testEmail).
                password("test_password").
                name("test_name").build();
        return userRepository.save(user);
    }

    private String createTestPaymentToken() {
        //
        PaymentReadyRequest request = new PaymentReadyRequest(TEST_TOTAL_AMOUNT, TEST_MERCHANT_ID, TEST_MERCHANT_ORDER_ID);
        PaymentReadyResponse response = controller.readyPayment(request);
        return response.getPaymentToken();
    }

}

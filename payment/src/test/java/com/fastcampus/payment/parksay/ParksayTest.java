package com.fastcampus.payment.parksay;

import com.fastcampus.payment.PaymentApplication;
import com.fastcampus.payment.common.util.CommonUtil;
import com.fastcampus.payment.controller.PaymentController;
import com.fastcampus.payment.dto.*;
import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.entity.PaymentStatus;
import com.fastcampus.paymentmethod.entity.*;
import com.fastcampus.paymentmethod.repository.CardInfoRepository;
import com.fastcampus.paymentmethod.repository.PaymentMethodRepository;
import com.fastcampus.paymentmethod.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
public class ParksayTest {


    @Value("${lifetime.qr}")
    private String ttlQr; // 단위: 초


    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    PaymentController controller;
    @Autowired
    CommonUtil commonUtil;

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
        TEST_CARD = createTestCardInfo("test_card_token", "test_card_company", TEST_METHOD);
        //
        System.out.println("before ========= " + TEST_TOKEN);
    }

    @Test
    void contextLoads() {
    }

    @Test
    @Order(2)
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
    @Order(3)
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
    @Order(4)
    public void cancelTest() throws Exception {
//        //
//        PaymentProgressResponse response = controller.progressPayment(TEST_TOKEN);
//        //
//        mockMvc.perform(
//                delete("/api/payments/{paymentToken}", TEST_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("CANCELED")
//        );

        // given
        PaymentExecutionRequest request = new PaymentExecutionRequest();
        request.setPaymentToken(TEST_TOKEN);
        request.setCardToken(TEST_CARD.getToken());
        request.setPaymentMethodType(TEST_METHOD.getType().toString());
        request.setUserId(TEST_USER.getUserId());
        controller.executePayment(request);

        // when
//        PaymentCancelRequest request = new PaymentCancelRequest(TEST_TOKEN);
        PaymentCancelResponse response = controller.cancelPayment(TEST_TOKEN);
        // then
        Assertions.assertEquals(TEST_MERCHANT_ORDER_ID, response.getMerchantOrderId());
        Assertions.assertEquals(PaymentStatus.CANCELED.toString(), response.getStatus());
        Assertions.assertEquals(TEST_TOKEN, response.getPaymentToken());
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
        cardInfoRepository.save(cardInfo);
        return cardInfo;
    }

    private PaymentMethod createTestPaymentMethod(PaymentMethodType type, User user) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(type);
        paymentMethod.setUseYn(UseYn.Y);
        paymentMethod.setUser(user);
        paymentMethod.setDescription(type.getDisplayName() + " 테스트");
        paymentMethodRepository.save(paymentMethod);
        return paymentMethod;
    }

    private User createTestUser() {
        Long time = new Date().getTime();
        String testEmail = Long.toBinaryString(time);
        User user = User.builder().
                email(testEmail).
                password("test_password").
                name("test_name").build();
        userRepository.save(user);
        return user;
    }

    private String createTestPaymentToken() {
        //
        PaymentReadyRequest request = new PaymentReadyRequest(TEST_TOTAL_AMOUNT, TEST_MERCHANT_ID, TEST_MERCHANT_ORDER_ID);
        PaymentReadyResponse response = controller.readyPayment(request);
        return response.getPaymentToken();
    }

}

package com.fastcampus.payment.parksay;

import com.fastcampus.payment.PaymentApplication;
import com.fastcampus.payment.common.util.CommonUtil;
import com.fastcampus.payment.controller.PaymentController;
import com.fastcampus.payment.dto.PaymentProgressRequest;
import com.fastcampus.payment.dto.PaymentProgressResponse;
import com.fastcampus.payment.dto.PaymentReadyRequest;
import com.fastcampus.payment.dto.PaymentReadyResponse;
import com.fastcampus.payment.repository.TransactionRepositoryRedis;
import com.fastcampus.paymentmethod.entity.PaymentMethodType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

@SpringBootTest(classes = PaymentApplication.class)
@Import(TestRedisConfig.class)
public class ParksayTest {

    @MockitoBean
    private TransactionRepositoryRedis transactionRepositoryRedis;

    @Autowired
    PaymentController controller;

    @Autowired
    CommonUtil commonUtil;

    @Value("${lifetime.qr}")
    private String ttlQr; // 단위: 초

    private static String TEST_TOKEN;
    private static Long TEST_TOTAL_AMOUNT;
    private static Long TEST_MERCHANT_ID;
    private static String TEST_MERCHANT_ORDER_ID;

    @BeforeEach
    public void beforeEach() {
        TEST_TOTAL_AMOUNT = 256329L;
        TEST_MERCHANT_ID = 245L;
        TEST_MERCHANT_ORDER_ID = "TEST_ORDER_21";
        PaymentReadyRequest request = new PaymentReadyRequest(TEST_TOTAL_AMOUNT, TEST_MERCHANT_ID, TEST_MERCHANT_ORDER_ID);
        PaymentReadyResponse response = controller.initiateTransaction(request);
        TEST_TOKEN = response.getPaymentToken();
        System.out.println("before ========= " + TEST_TOTAL_AMOUNT);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void readyTest() {
        //
        PaymentReadyRequest request = new PaymentReadyRequest(TEST_MERCHANT_ID, TEST_TOTAL_AMOUNT, TEST_MERCHANT_ORDER_ID);
        PaymentReadyResponse response = controller.initiateTransaction(request);
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
        PaymentProgressRequest request = new PaymentProgressRequest(TEST_TOKEN);
        PaymentProgressResponse response = controller.getTransactionProgress(request);
        //
        Assertions.assertEquals(TEST_TOTAL_AMOUNT, response.getAmount());
        Assertions.assertEquals(TEST_MERCHANT_ID, response.getMerchantId());
        Assertions.assertEquals(TEST_MERCHANT_ORDER_ID, response.getMerchantOrderId());
        Assertions.assertEquals(TEST_TOKEN, response.getPaymentToken());
    }


    @Test
    public void simpleTest() {
        System.out.println("PaymentMethodType.BANK_TRANSFER.toString() = " + PaymentMethodType.BANK_TRANSFER.toString());
    }
}

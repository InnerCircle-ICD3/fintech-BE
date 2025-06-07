package com.fastcampus.payment.parksay;

import com.fastcampus.payment.PaymentApplication;
import com.fastcampus.payment.controller.PaymentController;
import com.fastcampus.payment.dto.PaymentProgressRequest;
import com.fastcampus.payment.dto.PaymentProgressResponse;
import com.fastcampus.payment.dto.PaymentReadyRequest;
import com.fastcampus.payment.dto.PaymentReadyResponse;
import com.fastcampus.payment.repository.TransactionRepositoryRedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = PaymentApplication.class)
@Import(TestRedisConfig.class)
public class ParksayTest {

    @MockitoBean
    private TransactionRepositoryRedis transactionRepositoryRedis;

    @Autowired
    PaymentController controller;

    @Test
    void contextLoads() {
    }

//    @Test
//    public void readyTest() {
//        PaymentReadyRequest request = new PaymentReadyRequest("26", 143L, "54881");
//
//        PaymentReadyResponse response = controller.initiateTransaction(request);
//        System.out.println("response.getTransactionToken() = " + response.getTransactionToken());
//        System.out.println("response.getExpireAt() = " + response.getExpireAt());
//    }
//
//    @Test
//    public void progressTest() {
//        String testToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxcl90b2tlbiIsInRyYW5zYWN0aW9uSWQiOjEsImlhdCI6MTc0OTI2MTg4MCwiZXhwIjoxNzQ5MjYyMDYwfQ.ZseCmPuDNxoifV6ozCPBDAoURrh5CdAxEf3LfEtNwqs";
////        Assertions.assertThrows(RuntimeException.class, ()->{
////            PaymentProgressResponse response = controller.getTransactionProgress(testToken);
////            System.out.println("response = " + response.toString());
////        });
//        PaymentProgressResponse response = controller.getTransactionProgress(testToken);
//        System.out.println("response = " + response.toString());
//    }

}

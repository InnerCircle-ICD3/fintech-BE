package com.fastcampus.paymentcore.parksay;


import com.fastcampus.common.exception.exception.BadRequestException;
import com.fastcampus.paymentcore.PaymentCoreApplication;
import com.fastcampus.paymentcore.core.common.util.SystemParameterUtil;
import com.fastcampus.paymentcore.core.common.util.TokenHandler;
import com.fastcampus.paymentcore.core.dto.*;
import com.fastcampus.paymentcore.core.service.PaymentProgressService;
import com.fastcampus.paymentcore.core.service.PaymentReadyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.logging.Logger;

@SpringBootTest(classes = PaymentCoreApplication.class)
@EnableJpaRepositories(basePackages = {"com.fastcampus.paymentinfra.repository"})
public class MyTest {
    Logger logger = Logger.getLogger(MyTest.class.getName());

    @Autowired
    PaymentReadyService readyService;

    @Autowired
    PaymentProgressService progressService;

    @Autowired
    SystemParameterUtil systemParameter;

    @Autowired
    TokenHandler tokenHandler;

    @Test
    public void myTest1() {
        logger.info("hello world!");
    }
    
    @Test
    public void readyPayment() {
        //
        PaymentReadyRequest request =  new PaymentReadyRequest(1L, 2L, "test3");
        PaymentReadyResponse response = readyService.readyPayment(request);
        //
        logger.info("test result >> readyPayment: " + response.toString());
    }

    @Test
    public void progressPayment() {

        // 일단 아무 값이 넣음
        String qrToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxcl90b2tlbiIsInRyYW5zYWN0aW9uSWQiOjEsImlhdCI6MTc0ODcwNTEzOSwiZXhwIjoxNzQ4NzA1MzE5fQ.prHo1VomXsdbSAUUCMM1hPFYhCqeAA1rXqLxxQpFEWI";
        String cardToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxcl90b2tlbiIsInRyYW5zYWN0aW9uSWQiOjEsImlhdCI6MTc0ODcwNTEzOSwiZXhwIjoxNzQ4NzA1MzE5fQ.prHo1VomXsdbSAUUCMM1hPFYhCqeAA1rXqLxxQpFEWI";

        PaymentProgressRequest request = new PaymentProgressRequest(qrToken, cardToken);
        Assertions.assertThrows(BadRequestException.class, ()->{
            PaymentProgressResponse response = progressService.progressPayment(request);
            logger.info(response.toString());
        });


    }

    @Test
    public void systemParameterUtilTest() {
        String result = systemParameter.getProperty("hello");
        logger.info("===============================================");
        logger.info(result);
    }

    @Test
    public void tokenHandlerTest() {
        Long id = 34L;
        String result = tokenHandler.generateTokenWithTransactionId(id);
        logger.info(result);
    }
}

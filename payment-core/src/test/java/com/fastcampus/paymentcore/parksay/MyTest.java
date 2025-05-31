package com.fastcampus.paymentcore.parksay;


import com.fastcampus.paymentcore.PaymentCoreApplication;
import com.fastcampus.paymentcore.core.common.util.SystemParameterUtil;
import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;
import com.fastcampus.paymentcore.core.dto.PaymentReadyRequestDto;
import com.fastcampus.paymentcore.core.dto.ProgressTransactionRequest;
import com.fastcampus.paymentcore.core.dto.ResponsePaymentReady;
import com.fastcampus.paymentcore.core.service.PaymentProgressService;
import com.fastcampus.paymentcore.core.service.PaymentReadyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@SpringBootTest(classes = PaymentCoreApplication.class)
public class MyTest {
    Logger logger = Logger.getLogger(MyTest.class.getName());

    @Autowired
    PaymentReadyService readyService;

    @Autowired
    PaymentProgressService progressService;

    @Autowired
    SystemParameterUtil systemParameter;

    @Test
    public void myTest1() {
        logger.info("hello world!");
    }

    @Test
    public void readyPayment() {
        PaymentReadyRequestDto request = new PaymentReadyRequestDto(
                1L,
                "1",
                1L
        );
        ResponsePaymentReady response = readyService.readyPayment(request);
        logger.info(response.toString());
    }

    @Test
    public void progressPayment() {
        String qrToken = "qr token from sdk";
        ProgressTransactionRequest request = new ProgressTransactionRequest(qrToken);
        PaymentProgressDto paymentProgressDto = progressService.progressPayment(request);
        logger.info(paymentProgressDto.toString());
    }

    @Test
    public void systemParameterUtilTest() {
        String result = systemParameter.getProperty("hello");
        logger.info("===============================================");
        logger.info(result);
    }
}

package com.fastcampus.paymentcore.parksay;


import com.fastcampus.paymentcore.PaymentCoreApplication;
import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;
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

    @Test
    public void myTest1() {
        logger.info("hello world!");
    }

    @Test
    public void readyPayment() {
        //
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hello", "world");
        String qrToken = readyService.readyPayment(paramMap);
        //
        logger.info(qrToken);
    }

    @Test
    public void progressPayment() {
        //
        String qrToken = "qr token from sdk";
        PaymentProgressDto paymentProgressDto = progressService.progressPayment(qrToken);
        //
        logger.info(paymentProgressDto.toString());
    }

}

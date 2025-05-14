package com.fastcampus.paymentcore.parksay;


import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;
import com.fastcampus.paymentcore.core.impl.PaymentProgressServiceImpl;
import com.fastcampus.paymentcore.core.impl.PaymentReadyServiceImpl;
import com.fastcampus.paymentcore.core.service.PaymentProgessService;
import com.fastcampus.paymentcore.core.service.PaymentReadyService;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MyTest {
    Logger logger = Logger.getLogger(MyTest.class.getName());
    @Test
    public void myTest1() {
        logger.info("hello world!");
    }


    @Test
    public void readyPayment() {
        PaymentReadyService service = new PaymentReadyServiceImpl();
        //
        Map<String, Object> paramMap = new HashMap<>();
        String qrToken = service.readyPayment(paramMap);
        //
        logger.info(qrToken);
    }

    @Test
    public void progressPayment() {
        PaymentProgessService service = new PaymentProgressServiceImpl();
        //
        String qrToken = "qr token from sdk";
        PaymentProgressDto paymentProgressDto = service.progressPayment(qrToken);
        //
        logger.info(paymentProgressDto.toString());
    }
}

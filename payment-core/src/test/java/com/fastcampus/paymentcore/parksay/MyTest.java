package com.fastcampus.paymentcore.parksay;


import com.fastcampus.paymentcore.PaymentCoreApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.logging.Logger;

@SpringBootTest(classes= PaymentCoreApplication.class)
@ExtendWith(SpringExtension.class)
public class MyTest {
    Logger logger = Logger.getLogger(MyTest.class.getName());
    @Test
    public void myTest1() {
        logger.info("hello world!");
    }
}

package com.fastcampus.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = PaymentApplication.class)
@ActiveProfiles("test")
class PaymentApplicationTests {

    @Test
    void contextLoads() {
    }

}

package com.fastcampus.paymentinfra;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes=PaymentInfraApplication.class)
@ActiveProfiles("infra")  // application-infra.yml 을 로드하게 만듦
class PaymentInfraApplicationTests {

    @Test
    void contextLoads() {
    }

}

package com.fastcampus.paymentinfra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fastcampus.paymentcore",      // core 서비스 모듈
        "com.fastcampus.paymentinfra"       // infra 모듈
})
public class PaymentInfraApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentInfraApplication.class, args);
    }

}

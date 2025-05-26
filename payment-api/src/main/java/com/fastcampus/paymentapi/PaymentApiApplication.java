package com.fastcampus.paymentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fastcampus.paymentapi",       // 현재 프로젝트
        "com.fastcampus.paymentcore",      // core 서비스 모듈
        "com.fastcampus.paymentinfra"
})
public class PaymentApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApiApplication.class, args);
    }
}

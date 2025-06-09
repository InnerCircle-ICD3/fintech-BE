package com.fastcampus.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.fastcampus.payment.entity",          // 현재 모듈 엔티티
        "com.fastcampus.paymentmethod.entity"     // 참조하는 모듈 엔티티
})
@EnableJpaRepositories(basePackages = {
        "com.fastcampus.payment.repository",
        "com.fastcampus.paymentmethod.repository"
})
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}

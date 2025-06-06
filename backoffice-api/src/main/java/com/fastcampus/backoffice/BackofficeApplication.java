package com.fastcampus.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.fastcampus.backoffice")
@EntityScan(basePackages = {
        "com.fastcampus.backoffice.entity",
        "com.fastcampus.payment.entity"
})
public class BackofficeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackofficeApplication.class, args);
    }
} 
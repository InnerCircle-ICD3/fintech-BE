package com.fastcampus.appusermanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.fastcampus.paymentmethod.entity")
@EnableJpaRepositories("com.fastcampus.paymentmethod.repository")
@ComponentScan(basePackages = {
        "com.fastcampus.paymentmethod"
})
public class AppuserManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppuserManageApplication.class, args);
    }

}

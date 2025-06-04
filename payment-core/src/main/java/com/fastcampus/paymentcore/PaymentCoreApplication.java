package com.fastcampus.paymentcore;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.fastcampus.paymentcore", "com.fastcampus.paymentinfra"},
	exclude = {
			org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
			org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
	})
@EnableJpaRepositories(basePackages = {"com.fastcampus.paymentinfra.repository"})
public class PaymentCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentCoreApplication.class, args);
	}

}

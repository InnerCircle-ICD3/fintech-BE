package com.fastcampus.paymentcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentCoreApplication.class, args);
		System.out.println("args = " + args);
	}

}

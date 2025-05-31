package com.fastcampus.paymentcore;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.fastcampus.paymentcore",      // core 서비스 모듈
		"com.fastcampus.paymentinfra"})
public class PaymentCoreApplication {

	public static void main(String[] args) {
		System.out.println("args = " + args);
	}

}

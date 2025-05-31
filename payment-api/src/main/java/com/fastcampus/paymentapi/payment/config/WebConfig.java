package com.fastcampus.paymentapi.payment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("*") // GET, POST, PUT, DELETE 등 모두 허용
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키 같은 인증정보 허용 (필요하면)
    }
}

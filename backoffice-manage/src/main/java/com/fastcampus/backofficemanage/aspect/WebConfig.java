package com.fastcampus.backofficemanage.aspect;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginIdResolver loginIdResolver;

    public WebConfig(LoginIdResolver loginIdResolver) {
        this.loginIdResolver = loginIdResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginIdResolver);
    }
}
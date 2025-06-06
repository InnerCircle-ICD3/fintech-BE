package com.fastcampus.payment.common.util;


import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SystemParameterUtil {
    private final Environment environment;

    public SystemParameterUtil(Environment environment) {
        this.environment = environment;
    }

    public String getProperty(String key) {
        return environment.getProperty(key);
    }

}

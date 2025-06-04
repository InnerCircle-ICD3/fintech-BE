package com.fastcampus.common.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.objenesis.Objenesis;
import org.springframework.stereotype.Component;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 주어진 객체를 JSON 문자열로 직렬화합니다.
     *
     * @param object 직렬화할 객체
     * @return 객체의 JSON 문자열 표현
     * @throws RuntimeException 직렬화에 실패한 경우 발생
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }
}

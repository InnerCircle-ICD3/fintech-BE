package com.fastcampus.common.util;


import org.springframework.stereotype.Component;

import java.util.UUID;

public class UUIDGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public static String generateWithPrefix(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString();
    }

}

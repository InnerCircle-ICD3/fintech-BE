package com.fastcampus.paymentcore.core.common.util;

import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class TokenHandler {



    public String generateTokenPaymentReady() {
        String token = UUID.randomUUID().toString().replace("-", "");
        return token;
//      TODO -발급한 token 이 이미 발급한 적이 있는지 중복 체크하는 로직인데... token db에 저장할지 redis 에 저장할지 결정되면 추후 구현
//        int maxRetry = 5;
//        for (int i = 0; i < maxRetry; i++) {
//            String redisKey = RedisKeys.QR_KEY_PREFIX + token;
//
//            if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
//                return token;
//            }
//        }
//        throw new HttpException(PaymentErrorCode.QR_GENERATION_FAILED);
    }


    public int decodeQrToken(String qrToken) {
        return 0;
    }


}

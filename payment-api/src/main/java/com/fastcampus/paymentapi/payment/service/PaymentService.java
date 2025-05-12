package com.fastcampus.paymentapi.payment.service;

import com.fastcampus.common.exception.BaseException;
import com.fastcampus.common.exception.PaymentErrorCode;
import com.fastcampus.paymentapi.payment.dto.PaymentRequest;
import com.fastcampus.paymentapi.payment.entity.Payment;
import com.fastcampus.paymentapi.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${sdk.ttl-qr}")
    private long qrTtlMinutes;

    public String createQr(PaymentRequest request) {
        if (request.getMerchantId() == null || request.getMerchantOrderId() == null || request.getAmount() == null) {
            throw new BaseException(PaymentErrorCode.INVALID_PAYMENT_REQUEST);
        }

        boolean exists = paymentRepository.existsByMerchantIdAndMerchantOrderId(
                request.getMerchantId(), request.getMerchantOrderId()
        );

        if (exists) {
            throw new BaseException(PaymentErrorCode.DUPLICATE_ORDER);
        }

        // ✅ 중복되지 않는 UUID 생성 및 Redis 저장
        String qrToken;
        String redisKey;
        int retry = 0;
        do {
            qrToken = UUID.randomUUID().toString().replace("-", "");
            redisKey = "qr:" + qrToken;
            retry++;
            if (retry > 5) {
                throw new BaseException(PaymentErrorCode.QR_GENERATION_FAILED); // 에러 코드 재사용
            }
        } while (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey)));

        redisTemplate.opsForValue().set(redisKey, "valid", Duration.ofMinutes(qrTtlMinutes));

        // ✅ 결제 정보 저장
        String qrUrl = "https://pay.example.com/qr?token=" + qrToken;
        Payment payment = new Payment(
                request.getSdkKey(),
                request.getMerchantId(),
                request.getMerchantOrderId(),
                request.getAmount(),
                qrUrl
        );
        paymentRepository.save(payment);

        return qrUrl;
    }
}

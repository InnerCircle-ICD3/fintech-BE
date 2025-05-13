package com.fastcampus.paymentapi.payment.service;

import com.fastcampus.common.exception.HttpException;
import com.fastcampus.common.exception.PaymentErrorCode;
import com.fastcampus.paymentapi.payment.dto.PaymentRequest;
import com.fastcampus.paymentapi.payment.dto.QrVerificationResponse;
import com.fastcampus.paymentapi.payment.entity.Payment;
import com.fastcampus.paymentapi.payment.entity.PaymentStatus;
import com.fastcampus.paymentapi.payment.entity.QrStatus;
import com.fastcampus.paymentapi.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final Clock clock = Clock.system(ZoneId.of("Asia/Seoul"));

    @Value("${sdk.ttl-qr}")
    private long qrTtlMinutes;

    public String createQr(PaymentRequest request) {
        if (request.getMerchantId() == null || request.getMerchantOrderId() == null || request.getAmount() == null) {
            throw new HttpException(PaymentErrorCode.INVALID_PAYMENT_REQUEST);
        }

        if (paymentRepository.existsByMerchantIdAndMerchantOrderId(
                request.getMerchantId(), request.getMerchantOrderId())) {
            throw new HttpException(PaymentErrorCode.DUPLICATE_ORDER);
        }

        String qrToken = generateUniqueQrToken();
        String redisKey = "qr:" + qrToken;

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expiresAt = now.plusMinutes(qrTtlMinutes);

        redisTemplate.opsForValue().set(redisKey, "valid", Duration.ofMinutes(qrTtlMinutes));

        Payment payment = new Payment(
                request.getSdkKey(),
                request.getMerchantId(),
                request.getMerchantOrderId(),
                request.getAmount(),
                qrToken,
                expiresAt,
                now
        );
        paymentRepository.save(payment);

        return String.format("https://pay.example.com/qr?token=%s&expires=%s",
                qrToken,
                expiresAt
        );
    }

    private String generateUniqueQrToken() {
        int maxRetry = 5;

        for (int i = 0; i < maxRetry; i++) {
            String token = UUID.randomUUID().toString().replace("-", "");
            String redisKey = "qr:" + token;

            if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                return token;
            }
        }

        throw new HttpException(PaymentErrorCode.QR_GENERATION_FAILED);
    }

    public QrVerificationResponse verifyQrStatus(String token) {
        return paymentRepository.findByQrToken(token)
                .map(payment -> {
                    LocalDateTime now = LocalDateTime.now(clock);

                    if (payment.getExpiresAt().isBefore(now)) {
                        return new QrVerificationResponse(QrStatus.EXPIRED, payment.getStatus(), payment.getExpiresAt(), "QR이 만료되었습니다.");
                    }

                    if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.FAILED) {
                        return new QrVerificationResponse(QrStatus.USED, payment.getStatus(), payment.getExpiresAt(), "이미 처리된 결제입니다.");
                    }

                    return new QrVerificationResponse(QrStatus.VALID, payment.getStatus(), payment.getExpiresAt(), "사용 가능한 QR입니다.");
                })
                .orElse(new QrVerificationResponse(QrStatus.INVALID, null, null, "QR 정보가 존재하지 않습니다."));
    }
}

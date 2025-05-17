package com.fastcampus.paymentapi.initqr.service;

import com.fastcampus.common.constant.RedisKeys;
import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.paymentapi.initqr.dto.QrInitRequest;
import com.fastcampus.paymentapi.initqr.dto.QrInitResponse;
import com.fastcampus.paymentapi.initqr.dto.QrVerificationResponse;
import com.fastcampus.paymentapi.initqr.entity.Payment;
import com.fastcampus.paymentapi.initqr.entity.PaymentStatus;
import com.fastcampus.paymentapi.initqr.repository.PaymentRepository;
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

    public QrInitResponse createQr(QrInitRequest request) {
        request.validateOrThrow();
        validateDuplicateOrder(request);

        String qrToken = generateUniqueQrToken();
        cacheQrToken(qrToken);

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expiresAt = now.plusMinutes(qrTtlMinutes);

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

        return new QrInitResponse(qrToken, expiresAt);
    }

    public QrVerificationResponse verifyQrStatus(String token) {
        return paymentRepository.findByQrToken(token)
                .map(payment -> evaluateQrStatus(payment, LocalDateTime.now(clock)))
                .orElse(QrVerificationResponse.invalid("QR 정보가 존재하지 않습니다."));
    }

    private void validateDuplicateOrder(QrInitRequest request) {
        if (paymentRepository.existsByMerchantIdAndMerchantOrderId(request.getMerchantId(), request.getMerchantOrderId())) {
            throw new HttpException(PaymentErrorCode.DUPLICATE_ORDER);
        }
    }

    private String generateUniqueQrToken() {
        int maxRetry = 5;

        for (int i = 0; i < maxRetry; i++) {
            String token = UUID.randomUUID().toString().replace("-", "");
            String redisKey = RedisKeys.QR_KEY_PREFIX + token;

            if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                return token;
            }
        }

        throw new HttpException(PaymentErrorCode.QR_GENERATION_FAILED);
    }

    private void cacheQrToken(String qrToken) {
        String redisKey = RedisKeys.QR_KEY_PREFIX + qrToken;
        redisTemplate.opsForValue().set(redisKey, "valid", Duration.ofMinutes(qrTtlMinutes));
    }

    private QrVerificationResponse evaluateQrStatus(Payment payment, LocalDateTime now) {
        if (payment.getExpiresAt().isBefore(now)) {
            return QrVerificationResponse.expired(payment.getStatus(), payment.getExpiresAt(), "QR이 만료되었습니다.");
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.FAILED) {
            return QrVerificationResponse.used(payment.getStatus(), payment.getExpiresAt(), "이미 처리된 결제입니다.");
        }

        return QrVerificationResponse.valid(payment.getStatus(), payment.getExpiresAt(), "사용 가능한 QR입니다.");
    }
}

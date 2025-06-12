package com.fastcampus.payment.service;

import com.fastcampus.payment.common.exception.CommonException;
import com.fastcampus.payment.common.exception.base.HttpException;
import com.fastcampus.payment.common.exception.error.PaymentErrorCode;
import com.fastcampus.payment.common.util.TokenHandler;
import com.fastcampus.payment.common.idem.Idempotent;
import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.repository.PaymentRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentProgressService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentProgressService.class);

    private final PaymentRepository paymentRepository;

    private final TokenHandler tokenHandler;

    @Idempotent
    public Payment progressPayment(String token) {
        Optional<Payment> payment = Optional.empty();
            try {
                // QR 토큰에서 거래 ID 디코딩
                Long paymentId = tokenHandler.decodeQrToken(token);
                // 거래 조회
                payment = paymentRepository.findById(paymentId);
            } catch (ExpiredJwtException e) {
                // tokenHandler.decodeQrToken(); 중에 token 만료 exception 발생 시
                throw new HttpException(PaymentErrorCode.PAYMENT_EXPIRED);
        }
        
        // payment 있으면 반환하고 없으면 NOT FOUND 예외 발생
        return payment.orElseThrow(() -> new HttpException(PaymentErrorCode.PAYMENT_NOT_FOUND));
    }
}

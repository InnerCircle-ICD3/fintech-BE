package com.fastcampus.paymentcore.core.common.util;

import com.fastcampus.paymentinfra.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class TokenHandler {


    @Autowired
    TransactionRepository transactionRepository;

    public String generateTokenPaymentReady() {
        String token;
        do {
            token = UUID.randomUUID().toString().replace("-", "");
        } while (transactionRepository.findByTransactionToken(token).isPresent());  // 중복되는 값이 있다면 true 라면 반복

        return token;
    }

    public int decodeQrToken(String qrToken) {
        return 0;
    }


}

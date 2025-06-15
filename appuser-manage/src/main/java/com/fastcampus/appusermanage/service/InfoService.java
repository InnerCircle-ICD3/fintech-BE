package com.fastcampus.appusermanage.service;

import com.fastcampus.appusermanage.dto.transaction.UserTransactionResponse;
import com.fastcampus.appusermanage.jwt.JwtProvider;
import com.fastcampus.common.exception.code.AuthErrorCode;
import com.fastcampus.common.exception.code.CardErrorCode;
import com.fastcampus.common.exception.exception.NotFoundException;
import com.fastcampus.common.exception.exception.UnauthorizedException;
import com.fastcampus.paymentmethod.entity.CardInfo;
import com.fastcampus.paymentmethod.entity.User;
import com.fastcampus.paymentmethod.repository.CardInfoRepository;
import com.fastcampus.paymentmethod.repository.ReadOnlyTransactionRepository;
import com.fastcampus.paymentmethod.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final ReadOnlyTransactionRepository transactionRepository;
    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public List<UserTransactionResponse> getMyTransactionHistory(String authorizationHeader) {
        String email = extractEmailFromHeader(authorizationHeader);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_ID));

        return transactionRepository.findByPayment_User_UserId(user.getUserId())
                .stream()
                .map(tx -> UserTransactionResponse.builder()
                        .transactionId(tx.getId())
                        .amount(tx.getAmount())
                        .status(tx.getStatus())
                        .cardToken(tx.getCardToken())
                        .createdAt(tx.getCreatedAt())
                        .build())
                .toList();
    }

    public List<UserTransactionResponse> getTransactionHistoryByCard(String authorization, String cardToken) {
        String email = jwtProvider.getSubject(resolveBearerToken(authorization));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_ID));

        CardInfo card = cardInfoRepository.findByToken(cardToken)
                .orElseThrow(() -> new NotFoundException(CardErrorCode.NOT_FOUND_CARD));

        if (!card.getPaymentMethod().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException(CardErrorCode.UNAUTHORIZED_CARD_ACCESS);
        }

        return transactionRepository.findByCardToken(cardToken)
                .stream()
                .map(tx -> UserTransactionResponse.builder()
                        .transactionId(tx.getId())
                        .amount(tx.getAmount())
                        .status(tx.getStatus())
                        .cardToken(tx.getCardToken())
                        .createdAt(tx.getCreatedAt())
                        .build())
                .toList();
    }

    private String extractEmailFromHeader(String header) {
        if (header == null || header.isBlank()) {
            throw new NotFoundException(AuthErrorCode.MISSING_ACCESS_TOKEN);
        }
        String token = header.startsWith("Bearer ") ? header.substring(7) : header;
        return jwtProvider.getSubject(token);
    }

    private String resolveBearerToken(String header) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException(AuthErrorCode.MISSING_ACCESS_TOKEN);
        }
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }
}

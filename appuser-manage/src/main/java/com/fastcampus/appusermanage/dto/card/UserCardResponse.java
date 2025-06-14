package com.fastcampus.appusermanage.dto.card;

import com.fastcampus.paymentmethod.entity.CardType;
import com.fastcampus.paymentmethod.entity.CardInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserCardResponse {

    private String token;
    private String maskedCardNumber;
    private String expiryDate;
    private String cardCompany;
    private CardType type;
    private LocalDateTime createdAt;
    private String paymentMethodName;

    public static UserCardResponse from(CardInfo card) {
        return new UserCardResponse(
                card.getToken(),
                maskCardNumber(card.getCardNumber()),
                card.getExpiryDate(),
                card.getCardCompany(),
                card.getType(),
                card.getCreatedAt(),
                card.getPaymentMethod().getName()
        );
    }

    private static String maskCardNumber(String cardNumber) {
        return cardNumber.replaceAll("\\d{4}-\\d{4}-(\\d{4})-\\d{4}", "0000-****-****-$1");
    }
}

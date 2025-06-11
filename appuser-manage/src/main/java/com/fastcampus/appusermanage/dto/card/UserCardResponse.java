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
    private String maskedCardNumber;  // 마스킹된 카드번호
    private String expiryDate;
    private String cardCompany;
    private CardType type;
    private LocalDateTime createdAt;

    // 정적 팩토리 메서드로 생성
    public static UserCardResponse from(CardInfo card) {
        return new UserCardResponse(
                card.getToken(),
                maskCardNumber(card.getCardNumber()),
                card.getExpiryDate(),
                card.getCardCompany(),
                card.getType(),
                card.getCreatedAt()
        );
    }

    private static String maskCardNumber(String cardNumber) {
        // 예: 1234-****-****-5678
        return cardNumber.replaceAll("\\d{4}-\\d{4}-(\\d{4})-\\d{4}", "0000-****-****-$1");
    }
}

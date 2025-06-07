package com.fastcampus.appusermanage.service;

import com.fastcampus.appusermanage.dto.card.UserCardRegisterRequest;
import com.fastcampus.appusermanage.dto.card.UserCardResponse;
import com.fastcampus.paymentmethod.entity.User;
import com.fastcampus.paymentmethod.entity.UserCard;
import com.fastcampus.appusermanage.jwt.JwtProvider;
import com.fastcampus.paymentmethod.repository.CardRepository;
import com.fastcampus.paymentmethod.repository.UserRepository;
import com.fastcampus.common.exception.code.AuthErrorCode;
import com.fastcampus.common.exception.code.CardErrorCode;
import com.fastcampus.common.exception.exception.NotFoundException;
import com.fastcampus.common.exception.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCardService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 카드 등록
     */
    @Transactional
    public void registerCard(String authorizationHeader, UserCardRegisterRequest request) {
        User user = extractUserFromHeader(authorizationHeader);

        UserCard userCard = UserCard.builder()
                .user(user)
                .cardNumber(request.getCardNumber())
                .expiryDate(request.getExpiryDate())
                .birthDate(request.getBirthDate())
                .cardPw(request.getCardPw())
                .cvc(request.getCvc())
                .paymentPassword(passwordEncoder.encode(request.getPaymentPassword()))
                .cardCompany(request.getCardCompany())
                .type(request.getType())
                .build();
        userCard.generateToken();

        cardRepository.save(userCard);
    }

    /**
     * 카드 삭제
     */
    @Transactional
    public void deleteCard(String authorizationHeader, String cardToken) {
        User user = extractUserFromHeader(authorizationHeader);
        UserCard card = extractUserCardForUser(user, cardToken);

        user.getUserCards().remove(card);  // orphanRemoval=true 덕분에 DB에서도 삭제됨
    }

    /**
     * 카드 유효성 검사
     */
    @Transactional(readOnly = true)
    public boolean isValidCard(String authorizationHeader, String cardToken) {
        User user = extractUserFromHeader(authorizationHeader);
        extractUserCardForUser(user, cardToken);  // 소유자 검증 포함
        return true;
    }

    /**
     * 카드 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserCardResponse> getMyCards(String authorizationHeader) {
        User user = extractUserFromHeader(authorizationHeader);
        return user.getUserCards().stream()
                .map(UserCardResponse::from)
                .toList();
    }

    /**
     * 단일 카드 상세 조회
     */
    @Transactional(readOnly = true)
    public UserCardResponse getMyCardByToken(String authorizationHeader, String cardToken) {
        User user = extractUserFromHeader(authorizationHeader);
        UserCard card = extractUserCardForUser(user, cardToken);
        return UserCardResponse.from(card);
    }

    /**
     * 결제 비밀번호 등록/변경
     */
    @Transactional
    public void updatePaymentPassword(String authorizationHeader, String cardToken, String newPaymentPassword) {
        User user = extractUserFromHeader(authorizationHeader);
        UserCard card = extractUserCardForUser(user, cardToken);

        card.updatePaymentPassword(passwordEncoder.encode(newPaymentPassword));
    }

    // == 내부 메서드 ==
    private User extractUserFromHeader(String header) {
        String token = resolveBearerToken(header, AuthErrorCode.MISSING_ACCESS_TOKEN);
        String email = jwtProvider.getSubject(token);
        return findUserByEmail(email);
    }

    private String resolveBearerToken(String header, AuthErrorCode missingTokenError) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException(missingTokenError);
        }
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_ID));
    }

    private UserCard extractUserCardForUser(User user, String cardToken) {
        UserCard card = cardRepository.findByToken(cardToken)
                .orElseThrow(() -> new NotFoundException(CardErrorCode.NOT_FOUND_CARD));

        if (!card.getUser().getEmail().equals(user.getEmail())) {
            throw new UnauthorizedException(CardErrorCode.UNAUTHORIZED_CARD_ACCESS);
        }
        return card;
    }
}

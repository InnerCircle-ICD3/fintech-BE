package com.fastcampus.appusermanage.service;

import com.fastcampus.appusermanage.dto.card.UserCardRegisterRequest;
import com.fastcampus.appusermanage.dto.card.UserCardResponse;
import com.fastcampus.paymentmethod.entity.PaymentMethodType;
import com.fastcampus.paymentmethod.entity.User;
import com.fastcampus.paymentmethod.entity.CardInfo;
import com.fastcampus.paymentmethod.entity.PaymentMethod;
import com.fastcampus.appusermanage.jwt.JwtProvider;
import com.fastcampus.paymentmethod.repository.CardInfoRepository;
import com.fastcampus.paymentmethod.repository.PaymentMethodRepository;
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
    private final CardInfoRepository cardInfoRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 카드 등록
     */
    @Transactional
    public void registerCard(String authorizationHeader, UserCardRegisterRequest request) {
        User user = extractUserFromHeader(authorizationHeader);

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .type(PaymentMethodType.CARD)
                .name(request.getCardCompany())
                .build();

        CardInfo cardInfo = CardInfo.builder()
                .cardNumber(request.getCardNumber())
                .expiryDate(request.getExpiryDate())
                .birthDate(request.getBirthDate())
                .cardPw(request.getCardPw())
                .cvc(request.getCvc())
                .paymentPassword(passwordEncoder.encode(request.getPaymentPassword()))
                .cardCompany(request.getCardCompany())
                .type(request.getType())
                .paymentMethod(paymentMethod)
                .build();
        cardInfo.generateToken();

        paymentMethod.setCardInfo(cardInfo);

        // cardInfo만 저장해도 cascade로 인해 paymentMethod도 저장됨
        cardInfoRepository.save(cardInfo);
    }

    /**
     * 카드 삭제
     */
    @Transactional
    public void deleteCard(String authorizationHeader, String cardToken) {
        User user = extractUserFromHeader(authorizationHeader);
        PaymentMethod method = extractUserCardForUser(user, cardToken);

        user.getPaymentMethods().remove(method); // orphanRemoval = true로 cardInfo도 함께 삭제됨
    }

    /**
     * 카드 유효성 검사
     */
    @Transactional(readOnly = true)
    public boolean isValidCard(String authorizationHeader, String cardToken) {
        User user = extractUserFromHeader(authorizationHeader);
        extractUserCardForUser(user, cardToken);
        return true;
    }

    /**
     * 카드 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserCardResponse> getMyCards(String authorizationHeader) {
        User user = extractUserFromHeader(authorizationHeader);
        return user.getPaymentMethods().stream()
                .map(PaymentMethod::getCardInfo)
                .map(UserCardResponse::from)
                .toList();
    }

    /**
     * 단일 카드 상세 조회
     */
    @Transactional(readOnly = true)
    public UserCardResponse getMyCardByToken(String authorizationHeader, String cardToken) {
        User user = extractUserFromHeader(authorizationHeader);
        return UserCardResponse.from(extractUserCardForUser(user, cardToken).getCardInfo());
    }

    /**
     * 결제 비밀번호 변경
     */
    @Transactional
    public void updatePaymentPassword(String authorizationHeader, String cardToken, String newPaymentPassword) {
        User user = extractUserFromHeader(authorizationHeader);
        CardInfo cardInfo = extractUserCardForUser(user, cardToken).getCardInfo();
        cardInfo.updatePaymentPassword(passwordEncoder.encode(newPaymentPassword));
    }

    // == 내부 유틸 메서드 ==
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

    private PaymentMethod extractUserCardForUser(User user, String cardToken) {
        CardInfo card = cardInfoRepository.findByToken(cardToken)
                .orElseThrow(() -> new NotFoundException(CardErrorCode.NOT_FOUND_CARD));
        PaymentMethod method = card.getPaymentMethod();
        if (!method.getUser().getEmail().equals(user.getEmail())) {
            throw new UnauthorizedException(CardErrorCode.UNAUTHORIZED_CARD_ACCESS);
        }
        return method;
    }
}

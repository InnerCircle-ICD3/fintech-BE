package com.fastcampus.payment;

import com.fastcampus.payment.common.exception.BadRequestException;
import com.fastcampus.payment.dto.PaymentExecutionRequest;  // 🔥 타입 변경
import com.fastcampus.payment.dto.PaymentProgressResponse;
import com.fastcampus.payment.entity.CardInfo;
import com.fastcampus.payment.entity.PaymentMethod;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.entity.TransactionStatus;

import com.fastcampus.payment.repository.CardInfoRepository;
import com.fastcampus.payment.repository.PaymentMethodRepository;
import com.fastcampus.payment.repository.RedisTransactionRepository;
import com.fastcampus.payment.repository.TransactionRepository;

import com.fastcampus.payment.service.PaymentExecutionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional  // 🔥 클래스 레벨에!
@Rollback(true) // 🔥 클래스 레벨에!
@ActiveProfiles("test")

class PaymentExecutionServiceImplTest {

    @Autowired
    private PaymentExecutionService paymentExecutionService;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private TransactionRepository transactionRepository;




    @Test
    @DisplayName("결제 실행 성공 - 통합 테스트")
    void executePayment_Success() {

        // Given: 테스트 데이터 준비
        // 1. 카드 정보 생성 (setter 사용)
        CardInfo testCard = new CardInfo();
        testCard.setToken("test_card_123");
        testCard.setUserId(1L);
        testCard.setCardCompany("VISA");
        testCard.setLast4("1234");
        testCard.setType("CREDIT");
        cardInfoRepository.save(testCard);

        // 2. 결제 수단 생성 (setter 사용)
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType("CARD");
        paymentMethod.setIsActive(true);
        paymentMethodRepository.save(paymentMethod);

        // 3. 거래 생성 (setter 사용)
        Transaction transaction = new Transaction();
        transaction.setTransactionToken("test_transaction_123");
        transaction.setMerchantId(1L);
        transaction.setMerchantOrderId("ORDER_123");
        transaction.setAmount(10000L);
        transaction.setStatus(TransactionStatus.READY);
        transactionRepository.save(transaction);

        // 4. 요청 생성 (PaymentExecutionRequest 사용)
        PaymentExecutionRequest request = new PaymentExecutionRequest();  // 🔥 타입 변경
        request.setTransactionToken("test_transaction_123");
        request.setCardToken("test_card_123");
        request.setPaymentMethodType("CARD");

        // When: 결제 실행
        PaymentProgressResponse response = paymentExecutionService.execute(request);  // 🔥 메서드명 변경

        // Then: 결과 검증
        assertThat(response.getTransactionToken()).isEqualTo("test_transaction_123");
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getCardInfo().getToken()).isEqualTo("test_card_123");
        assertThat(response.getPaymentMethod().getType()).isEqualTo("CARD");
        assertThat(response.getApprovalResult()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 카드 토큰 - 예외 발생")
    void executePayment_CardNotFound_ThrowsException() {
        // Given: 거래는 생성하되, 카드는 생성하지 않음
        Transaction transaction = createTestTransaction("test_transaction_456");
        transactionRepository.save(transaction);

        PaymentMethod paymentMethod = createTestPaymentMethod("CARD");
        paymentMethodRepository.save(paymentMethod);

        PaymentExecutionRequest request = new PaymentExecutionRequest();  // 🔥 타입 변경
        request.setTransactionToken("test_transaction_456");
        request.setCardToken("invalid_card_token");  // 🚨 존재하지 않는 토큰
        request.setPaymentMethodType("CARD");

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> paymentExecutionService.execute(request))  // 🔥 메서드명 변경
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("카드 정보를 찾을 수 없습니다");
    }

    // 🔧 테스트 헬퍼 메서드들
    private CardInfo createTestCardInfo(String token, String company) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setToken(token);
        cardInfo.setUserId(1L);
        cardInfo.setCardCompany(company);
        cardInfo.setLast4("1234");
        cardInfo.setType("CREDIT");
        return cardInfo;
    }

    private PaymentMethod createTestPaymentMethod(String type) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(type);
        paymentMethod.setIsActive(true);
        return paymentMethod;
    }

    private Transaction createTestTransaction(String token) {
        Transaction transaction = new Transaction();
        transaction.setTransactionToken(token);
        transaction.setMerchantId(1L);
        transaction.setMerchantOrderId("ORDER_123");
        transaction.setAmount(10000L);
        transaction.setStatus(TransactionStatus.READY);
        return transaction;
    }
}
package com.fastcampus.paymentmethod.config;

import com.fastcampus.paymentmethod.entity.PaymentMethod;
import com.fastcampus.paymentmethod.entity.PaymentMethodType;
import com.fastcampus.paymentmethod.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션 시작 시 결제 방식 초기 데이터를 로딩하는 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMethodInitializer implements ApplicationRunner {

    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("결제 방식 초기 데이터 로딩 시작");
        initializePaymentMethods();
        log.info("결제 방식 초기 데이터 로딩 완료");
    }

    private void initializePaymentMethods() {
        // 모든 Enum 값을 DB에 등록
        for (PaymentMethodType type : PaymentMethodType.values()) {
            if (!paymentMethodRepository.existsByType(type)) {
                PaymentMethod paymentMethod = createPaymentMethod(type);
                paymentMethodRepository.save(paymentMethod);
                log.info("결제 방식 추가: {} ({})", type.name(), type.getDisplayName());
            } else {
                log.debug("결제 방식 이미 존재: {} ({})", type.name(), type.getDisplayName());
            }
        }
    }

    private PaymentMethod createPaymentMethod(PaymentMethodType type) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(type);
        paymentMethod.setUseYn(getDefaultActiveStatus(type));
        paymentMethod.setDescription(type.getDisplayName() + " 결제");
        return paymentMethod;
    }

    /**
     * 결제 방식별 기본 활성화 상태 설정
     * 실제 운영에서는 안전한 결제 방식부터 단계적으로 활성화
     */
    private String getDefaultActiveStatus(PaymentMethodType type) {
        return switch (type) {
            case CARD, BANK_TRANSFER -> "Y";  // 기본적으로 활성화
            case MOBILE_PAY, PAYPAL -> "Y";   // 모바일 결제도 활성화
            case CRYPTO -> "N";              // 암호화폐는 기본 비활성화
            case APPLE_PAY, GOOGLE_PAY -> "Y"; // 간편 결제 활성화
        };
    }
}
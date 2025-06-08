package com.fastcampus.payment.repository;


import com.fastcampus.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    /**
     * 지정된 가맹점 주문 ID에 해당하는 거래를 조회합니다.
     *
     * @param merchantOrderId 조회할 가맹점 주문 ID
     * @return 해당 주문 ID와 일치하는 거래가 있으면 Optional<Transaction>을 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    Optional<Payment> findByMerchantOrderId(String merchantOrderId);

    /****
     * 지정된 가맹점 ID에 해당하는 모든 거래 내역을 조회합니다.
     *
     * @param merchantId 거래를 조회할 가맹점의 ID
     * @return 해당 가맹점의 모든 거래 목록
     */
    List<Payment> findByMerchantId(Long merchantId);
    /**
     * 지정된 거래 토큰에 해당하는 거래 정보를 조회합니다.
     *
     * @param token 조회할 거래의 토큰 값
     * @return 거래 토큰이 일치하는 Payment 객체의 Optional, 없으면 Optional.empty()
     */
    Optional<Payment> findByToken(String token);

}

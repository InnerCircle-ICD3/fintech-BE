package com.fastcampus.payment.repository;

import com.fastcampus.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
 * 지정된 가맹점 주문 ID에 해당하는 거래를 조회합니다.
 *
 * @param merchantOrderId 조회할 가맹점 주문 ID
 * @return 해당 주문 ID와 일치하는 거래가 있으면 Optional<Transaction>을 반환하며, 없으면 빈 Optional을 반환합니다.
 */
Optional<Transaction> findByMerchantOrderId(String merchantOrderId);

    /****
 * 지정된 가맹점 ID에 해당하는 모든 거래 내역을 조회합니다.
 *
 * @param merchantId 거래를 조회할 가맹점의 ID
 * @return 해당 가맹점의 모든 거래 목록
 */
List<Transaction> findByMerchantId(Long merchantId);
    /**
 * 지정된 거래 토큰에 해당하는 거래 정보를 조회합니다.
 *
 * @param transactionToken 조회할 거래의 토큰 값
 * @return 거래 토큰이 일치하는 Transaction 객체의 Optional, 없으면 Optional.empty()
 */
Optional<Transaction> findByTransactionToken(String transactionToken);
}

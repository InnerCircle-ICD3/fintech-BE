package com.fastcampus.payment.repository;


import com.fastcampus.payment.entity.PaymentMethod;
import com.fastcampus.payment.entity.PaymentMethodType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByIsActiveTrue();
    Optional<PaymentMethod> findByType(PaymentMethodType type);

    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.type = :type")
    Optional<PaymentMethod> findByType(@Param("type") String typeString);

    // 특정 타입이 활성화되어 있는지 조회
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.type = :type AND pm.isActive = true")
    Optional<PaymentMethod> findActiveByType(@Param("type") PaymentMethodType type);

    // 활성화 상태별 조회
    List<PaymentMethod> findByIsActive(Boolean isActive);

    // 특정 타입이 존재하는지 확인
    boolean existsByType(PaymentMethodType type);
}

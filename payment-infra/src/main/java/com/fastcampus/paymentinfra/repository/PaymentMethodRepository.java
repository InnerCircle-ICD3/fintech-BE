package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByIsActiveTrue();
    Optional<PaymentMethod> findByType(String type);
}

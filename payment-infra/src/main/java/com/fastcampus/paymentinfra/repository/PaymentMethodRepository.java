package com.fastcampus.paymentinfra.repository;


import com.fastcampus.paymentcore.core.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByType(String type);
}

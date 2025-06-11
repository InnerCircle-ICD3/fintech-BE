package com.fastcampus.paymentmethod.repository;


import com.fastcampus.paymentmethod.entity.PaymentMethod;
import com.fastcampus.paymentmethod.entity.PaymentMethodType;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {


    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.type = :type and pm.user.id = :userId")
    List<PaymentMethod> findByUserIdAndMethodType(@Param("userId") Long userId, @Param("type")PaymentMethodType type);

    // 특정 타입이 존재하는지 확인
    boolean existsByType(PaymentMethodType type);


}

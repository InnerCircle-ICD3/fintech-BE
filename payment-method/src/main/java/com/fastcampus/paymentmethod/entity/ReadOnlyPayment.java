package com.fastcampus.paymentmethod.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadOnlyPayment {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long merchantId;

    @Column(nullable = false, length = 100)
    private String merchantOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReadOnlyPaymentStatus status;

    @Column(nullable = false)
    private Long totalAmount;
}

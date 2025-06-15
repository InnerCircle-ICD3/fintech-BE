package com.fastcampus.paymentmethod.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadOnlyTransaction {

    @Id
    private Long id;

    private Long amount;
    private String cardToken;

    @Enumerated(EnumType.STRING)
    private ReadOnlyPaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private ReadOnlyPayment payment;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

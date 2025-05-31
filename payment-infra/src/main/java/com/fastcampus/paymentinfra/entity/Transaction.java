package com.fastcampus.paymentinfra.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private final Long merchantId;
    private final String merchantOrderId;
    private final Long amount;

    @Enumerated(EnumType.STRING)
    @Setter
    private TransactionStatus status;

    @Column(nullable = true)
    private final String transactionToken;

    @Setter
    private String cardToken;

    @Column(columnDefinition = "TIMESTAMP")
    private final LocalDateTime expireAt;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    protected Transaction() {
        this.merchantId = null;
        this.merchantOrderId = null;
        this.amount = null;
        this.status = null;
        this.transactionToken = null;
        this.cardToken = null;
        this.expireAt = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

    public Transaction(Long merchantId, String merchantOrderId, Long amount,
                       TransactionStatus status, String transactionToken, String cardToken,
                       LocalDateTime createdAt, LocalDateTime expireAt) {
        this.merchantId = merchantId;
        this.merchantOrderId = merchantOrderId;
        this.amount = amount;
        this.status = status;
        this.transactionToken = transactionToken;
        this.cardToken = cardToken;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
    }
}

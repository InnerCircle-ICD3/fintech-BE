package com.fastcampus.payment.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Long merchantId;
    private String merchantOrderId;
    private Long amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String transactionToken;
    private String cardToken;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime expireAt;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;


    public void checkStatusAlreadyDone() {
        if (TransactionStatus.COMPLETED.equals(this.status)) {
            throw new IllegalStateException("이미 완료된 거래입니다.");
        }
    }
}
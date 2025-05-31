package com.fastcampus.paymentinfra.entity;

import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.common.exception.exception.BadRequestException;
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

    @Column(nullable = true)
    private String transactionToken;

    private String cardToken;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime expireAt;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    public void checkStatusAlreadyDone() {
        if (!TransactionStatus.REQUESTED.equals(this.getStatus())) {
            throw new BadRequestException(PaymentErrorCode.DUPLICATE_ORDER);
        }
    }
}

package com.fastcampus.payment.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    public void nullCheckRequiredParam() {
        List<Object> targetList = Arrays.asList(amount, merchantId, merchantOrderId);

        boolean isNull = targetList.stream().anyMatch(obj -> Objects.isNull(obj));
        if (isNull) {
            throw new RuntimeException("파라미터 내용을 확인해 주세요");
        }
    }
}
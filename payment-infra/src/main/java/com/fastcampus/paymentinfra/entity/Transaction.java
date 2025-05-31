package com.fastcampus.paymentinfra.entity;

import com.fastcampus.paymentinfra.type.TransactionStatus;
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
    @Column(nullable = false, unique = true)
    private String transactionToken; // 외부 요청용

    private String cardToken; // 사용된 카드 식별자 (CardInfo.token 기반)

    private Long merchantId;
    private String merchantOrderId;
    private Long amount;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;


    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime expireAt;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
}
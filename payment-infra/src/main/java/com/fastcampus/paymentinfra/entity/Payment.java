package com.fastcampus.paymentinfra.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long transactionId;
    private Long userId;
    private Long paymentMethod;
    private String paymentStatus;
    private Long paidAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_info_id")  // FK 컬럼명. DB에서 이 이름으로 FK 생성됨
    private CardInfo cardInfo;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime approvedAt;

    private String failReason;
    private Long lastTransactionId;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
}

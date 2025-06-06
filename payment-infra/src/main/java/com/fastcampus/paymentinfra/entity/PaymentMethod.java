package com.fastcampus.paymentinfra.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@Data
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentMethodId;

    @Column(nullable = false)  //"CATD", "BANK_TRANSFER", "PAYPAL" 등
    private String type;

    //추가 필드
    private String name;  // 카드 이름, 은행 이름 등
    private Boolean isActive; //활성화 여부

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
}
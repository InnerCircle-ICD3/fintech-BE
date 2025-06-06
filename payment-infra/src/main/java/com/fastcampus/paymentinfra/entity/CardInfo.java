package com.fastcampus.paymentinfra.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@Data
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardInfoId;

    private Long userId;



    @Column(nullable = false, unique = true)
    private String token;  // 카드 토큰, 외부 공개용 식별자

    private String type;  // CREDIT, DEBIT 등
    private String last4;
    private String cardCompany; // 카드사 이름, "VISA", "MASTER" 등

    //추가 필드
    private String issuerBank; // 발급 은행 이름
    private String maskedNumber; // 카드 번호 마스킹, "**** **** **** 1234" 형식

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
}
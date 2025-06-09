package com.fastcampus.paymentmethod.entity;


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

    //추가 필드
    private String name;  // 카드 이름, 은행 이름 등
    private Boolean isActive = true; //활성화 여부

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)  // Enum을 문자열로 DB에 저장
    @Column(name = "type", nullable = false, unique = true)
    private PaymentMethodType type;

    @Column(name = "description")
    private String description;

    public PaymentMethod(PaymentMethodType type, Boolean isActive) {
        this.type = type;
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
        this.description = type.getDisplayName();
    }

    public PaymentMethod() {
        super();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    // 하위 호환성을 위한 메서드들 (기존 String 기반 코드와 호환)
    public String getTypeString() {
        return type != null ? type.name() : null;
    }
}
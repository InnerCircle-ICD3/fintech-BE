package com.fastcampus.paymentmethod.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentMethodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK 컬럼명. DB에서 이 이름으로 FK 생성됨
    private User user;

    //추가 필드
    private String name;  // 카드 이름, 은행 이름 등
    @Column(length = 1)
    private String useYn = "Y"; //활성화 여부

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)  // Enum을 문자열로 DB에 저장
    @Column(name = "payment_method_type", nullable = false)
    private PaymentMethodType type;

    @Column(name = "description")
    private String description;

    public PaymentMethod(PaymentMethodType type, String useYn) {
        this.type = type;
        this.useYn = useYn;
        this.createdAt = LocalDateTime.now();
        this.description = type.getDisplayName();
    }

    public PaymentMethod() {
        super();
    }


}
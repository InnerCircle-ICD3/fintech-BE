package com.fastcampus.backofficemanage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchantId;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String loginPw;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String businessNumber;

    private String contactName;
    private String contactEmail;
    private String contactPhone;

    @Setter
    @Column(nullable = false)
    private String status;  // 예: ACTIVE, INACTIVE, DELETED 등

    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;

    @Setter
    @OneToOne(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Keys keys;

    public void updateInfo(String name, String businessNumber, String contactName,
                           String contactEmail, String contactPhone) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }
}

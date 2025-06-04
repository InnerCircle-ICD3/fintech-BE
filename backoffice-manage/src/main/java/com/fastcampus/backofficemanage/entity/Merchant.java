package com.fastcampus.backofficemanage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(nullable = false, length = 60)
    private String loginPw;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String businessNumber;

    @Column(length = 30)
    private String contactName;

    @Column(length = 50)
    private String contactEmail;

    @Column(length = 20)
    private String contactPhone;

    @Column(nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Keys keys;

    public void setKeys(Keys keys) {
        this.keys = keys;
        keys.setMerchant(this);
    }

    public void updateInfo(String name, String businessNumber, String contactName,
                           String contactEmail, String contactPhone) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }

    public void deactivate() {
        this.status = "INACTIVE";
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updatePassword(String encodedNewPassword) {
        this.loginPw = encodedNewPassword;
    }
}

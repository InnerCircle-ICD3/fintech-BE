package com.fastcampus.backofficemanage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sdk_key")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Keys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keysId;

    @Column(nullable = false, unique = true, length = 36)
    private String encryptedKey;

    @OneToOne
    @JoinColumn(name = "merchant_id", unique = true)
    private Merchant merchant;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime expiredAt;

    @Column(name = "expired_merchant_id")
    private Long expiredMerchantId;

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public static Keys createForMerchant(Merchant merchant) {
        Keys keys = Keys.builder()
                .encryptedKey(UUID.randomUUID().toString())
                .merchant(merchant)
                .build();
        merchant.setKeys(keys);
        return keys;
    }

    public void deactivate() {
        this.status = "INACTIVE";
    }

    public void activate() {
        this.status = "ACTIVE";
    }

    public void expire() {
        this.expiredAt = LocalDateTime.now();
        this.status = "INACTIVE";
        this.expiredMerchantId = this.merchant.getMerchantId(); // 과거 merchantId 기록용
    }
}

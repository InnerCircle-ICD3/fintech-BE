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
    @JoinColumn(name = "merchant_id", nullable = false, unique = true)
    private Merchant merchant;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime expiredAt;

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
}

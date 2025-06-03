package com.fastcampus.backofficemanage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "keys")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Keys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keysId;

    @Column(nullable = false, length = 36)
    private String encryptedKey;

    @OneToOne
    @JoinColumn(name = "merchant_id", nullable = false, unique = true)
    private Merchant merchant;

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
}

package com.fastcampus.backofficemanage.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String encryptedKey;

    @OneToOne
    @JoinColumn(name = "merchant_id", nullable = false, unique = true)
    private Merchant merchant;
}

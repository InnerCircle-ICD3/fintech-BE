package com.fastcampus.paymentinfra.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Immutable
@Table(name = "keys")
public class KeysReadOnly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long keysId;

    private final String encryptedKey;
    private final Long merchantId;

    @Column(columnDefinition = "TIMESTAMP", updatable = false)
    private final LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP")
    private final LocalDateTime updatedAt;

    protected KeysReadOnly() {
        this.keysId = null;
        this.encryptedKey = null;
        this.merchantId = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

    public KeysReadOnly(Long keysId, String encryptedKey, Long merchantId,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.keysId = keysId;
        this.encryptedKey = encryptedKey;
        this.merchantId = merchantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

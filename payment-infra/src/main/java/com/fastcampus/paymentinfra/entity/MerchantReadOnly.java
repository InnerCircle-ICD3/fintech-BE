package com.fastcampus.paymentinfra.entity;

import java.time.LocalDateTime;

public class MerchantReadOnly {

    private final Long merchantId;
    private final String name;
    private final String businessNumber;
    private final String contactName;
    private final String contactEmail;
    private final String contactPhone;
    private final String status;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    protected MerchantReadOnly() {
        this.merchantId = null;
        this.name = null;
        this.businessNumber = null;
        this.contactName = null;
        this.contactEmail = null;
        this.contactPhone = null;
        this.status = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

    public MerchantReadOnly(Long merchantId, String name, String businessNumber,
                            String contactName, String contactEmail, String contactPhone,
                            String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.merchantId = merchantId;
        this.name = name;
        this.businessNumber = businessNumber;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public String getName() {
        return name;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

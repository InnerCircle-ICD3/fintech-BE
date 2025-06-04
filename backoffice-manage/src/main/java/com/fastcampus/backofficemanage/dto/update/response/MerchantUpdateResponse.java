package com.fastcampus.backofficemanage.dto.update.response;

import lombok.Getter;

@Getter
public class MerchantUpdateResponse {
    private final String name;
    private final String businessNumber;
    private final String contactName;
    private final String contactEmail;
    private final String contactPhone;
    private final String status;

    public MerchantUpdateResponse(
            String name,
            String businessNumber,
            String contactName,
            String contactEmail,
            String contactPhone,
            String status
    ) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = status;
    }
}

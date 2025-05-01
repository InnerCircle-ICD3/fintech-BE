package com.fastcampus.backofficemanage.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantInfoResponse {

    private String name;
    private String businessNumber;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String status;
}

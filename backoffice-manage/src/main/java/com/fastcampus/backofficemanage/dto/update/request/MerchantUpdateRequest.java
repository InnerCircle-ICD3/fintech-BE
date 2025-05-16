package com.fastcampus.backofficemanage.dto.update.request;

import com.fastcampus.backofficemanage.dto.update.UpdateRequest;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantUpdateRequest extends UpdateRequest {

    private String name;
    private String businessNumber;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
}

package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminCreateMerchantResp {
    private Long id;

    private Long resourceId;

    private String name;
    private String type;

    private String username;

    private String contactName;
    private String contactPhone;
    private String address;

    private Integer status;
}

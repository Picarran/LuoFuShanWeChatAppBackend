package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUpdateMerchantResp  {
    private Long id;

    private String username;

    private String name;
    private String type;

    private Long resourceId;

    private String contactName;
    private String contactPhone;

    private Integer status;
}
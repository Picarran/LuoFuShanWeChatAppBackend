package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class AdminUpdateMerchantReq {
    private Long id;

    private String name;
    private String contactName;
    private String contactPhone;
    private Integer status;
}

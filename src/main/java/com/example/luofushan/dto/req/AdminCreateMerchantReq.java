package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class AdminCreateMerchantReq {
    private String username;
    private String password;

    private String name;
    private String type;

    private String contactName;
    private String contactPhone;
    private String address;

    private Long resourceId;
}

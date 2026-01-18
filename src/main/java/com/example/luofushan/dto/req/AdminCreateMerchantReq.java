package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class AdminCreateMerchantReq {
    private String username;
    private String password;

    private String name;
    private String type;

    private Long resourceId;
}

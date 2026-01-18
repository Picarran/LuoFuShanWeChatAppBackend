package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class AdminUpdateMerchantPasswordReq {
    private Long id;
    private String newPassword;
}

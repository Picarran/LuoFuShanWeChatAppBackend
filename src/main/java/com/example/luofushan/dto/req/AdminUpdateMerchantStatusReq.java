package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class AdminUpdateMerchantStatusReq {
    private Long id;
    private Integer status;
}
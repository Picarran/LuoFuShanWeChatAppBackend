package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class MerchantPointsUpdateReq {
    private Long userId;
    private Integer delta;  
    private String reason;  
}

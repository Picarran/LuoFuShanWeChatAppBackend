package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminCreateMerchantResp {
    private Long id;

    private String username;

    private String name;
    private String type;

    private Long resourceId;

    private Integer status;
}

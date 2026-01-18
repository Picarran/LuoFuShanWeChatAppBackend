package com.example.luofushan.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminMerchantDetailResp {
    private Long id;

    private String username;

    private String name;
    private String type;

    private Long resourceId;

    private Integer status;
    private LocalDateTime createdAt;

    private String contactName;
    private String contactPhone;
    private String resourceName;
}

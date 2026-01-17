package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class MerchantResourceBaseUpdateReq {
    private String name;
    private String coverImg;
    private Double latitude;
    private Double longitude;
}
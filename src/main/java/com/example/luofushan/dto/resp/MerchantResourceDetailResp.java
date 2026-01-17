package com.example.luofushan.dto.resp;

import lombok.Data;

import java.util.List;

@Data
public class MerchantResourceDetailResp {
    private Long resourceId;
    private String name;
    private String type;
    private String coverImg;
    private Double latitude;
    private Double longitude;
    private Integer hotScore;
    private List<MerchantResourceContentBlock> content;
}
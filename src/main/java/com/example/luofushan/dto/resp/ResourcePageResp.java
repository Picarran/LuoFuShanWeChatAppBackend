package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ResourcePageResp {
    private Long id;

    private String type; // 景点、住宿、餐饮、商家

    private String name;

    private String coverImg;

    private Double latitude;

    private Double longitude;

    private Integer hotScore;

}
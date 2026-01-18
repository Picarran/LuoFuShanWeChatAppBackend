package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class AdminSaveResourceReq {
    private Long id;

    private String type; // 景点、住宿、餐饮、商家

    private String name;

    private String coverImg;

    private Double latitude;

    private Double longitude;

    private Integer hotScore;

    private String contentJson;
}

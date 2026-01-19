package com.example.luofushan.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NearbyResourceContentResp {
    private Long id;
    private String name;
    private Double distance;       // 单位米
    private Double latitude;
    private Double longitude;
    private Integer hotScore;
    private String coverImg;
    private LocalDateTime createdAt;
    private String content;
}
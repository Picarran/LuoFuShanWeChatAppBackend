package com.example.luofushan.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NearbyResourceResp {
    private Long id;
    private String title;
    private Double distance; // 米
    private Integer hotScore;
    private String coverImg;
    private LocalDateTime createdAt;
}
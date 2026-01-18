package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class AdminSaveCheckinLocationReq {
    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    private Integer score;

    private String coverImg;
}

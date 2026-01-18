package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminSaveCheckinLocationResp {
    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    private Integer score;

    private Long todayHasCheckin;

    private String coverImg;
}

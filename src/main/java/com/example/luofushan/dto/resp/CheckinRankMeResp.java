package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckinRankMeResp {
    private String type;
    private Long rank;
    private Long checkinCount;
}

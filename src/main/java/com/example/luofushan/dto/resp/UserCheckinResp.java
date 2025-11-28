package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserCheckinResp {
    private Long checkinId;
    private Long locationId;
    private String locationName;
    private LocalDateTime checkinTime;
    private Integer score;
    private Long todayHasCheckin;
}
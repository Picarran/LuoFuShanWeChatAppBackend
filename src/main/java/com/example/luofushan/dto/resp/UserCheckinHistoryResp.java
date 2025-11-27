package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserCheckinHistoryResp {
    private Long locationId;
    private String locationName;
    private LocalDateTime checkinTime;
    private Integer score;
}
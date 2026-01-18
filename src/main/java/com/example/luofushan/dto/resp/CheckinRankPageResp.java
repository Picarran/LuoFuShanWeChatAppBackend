package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckinRankPageResp {
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String type;
    private Long rank;
    private Long checkinCount;
}

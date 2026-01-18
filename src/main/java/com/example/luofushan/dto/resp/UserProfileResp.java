package com.example.luofushan.dto.resp;

import lombok.Data;

@Data
public class UserProfileResp {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private Integer points;
    private Long weeklyCheckinCount;
    private Long daylyCheckinCount;
    private Long monthlyCheckinCount;
}

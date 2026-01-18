package com.example.luofushan.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp {
    private String token;
    private Long id;
    private String nickname;
    private String avatarUrl;
    private Integer points;
    private Long weeklyCheckinCount;
    private Long daylyCheckinCount;
    private Long monthlyCheckinCount;
}
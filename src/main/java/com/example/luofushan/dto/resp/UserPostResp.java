package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserPostResp {

    private Long id;

    private Long userId;

    private Long locationId;

    private String content;

    private List<String> images;

    private Integer likeCount;

    private Integer commentCount;

    private LocalDateTime postTime;
}
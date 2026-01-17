package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostListResp {
    private Long id;

    private Long userId;

    private String content;

    private Long locationId;
    private String locationName;
    private String imagesStr;

//    private List<String> images; // 真正要返回给前端
    private Double latitude;
    private Double longitude;
    private Double distance;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime postTime;
}
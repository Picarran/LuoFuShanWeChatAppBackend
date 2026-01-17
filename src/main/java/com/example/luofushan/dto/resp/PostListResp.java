package com.example.luofushan.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResp {
    private Long id;

    private Long userId;

    private String content;

    private Long locationId;
    private String locationName;

    private List<String> images;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime postTime;
}
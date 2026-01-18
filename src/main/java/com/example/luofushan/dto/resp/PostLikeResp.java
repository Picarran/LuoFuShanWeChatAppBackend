package com.example.luofushan.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLikeResp {
    private Long postId;
    private Integer likeCount;
    private boolean liked;
}

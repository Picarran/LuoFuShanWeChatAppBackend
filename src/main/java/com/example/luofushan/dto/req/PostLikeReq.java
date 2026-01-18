package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class PostLikeReq {
    private Long postId;
    private String action;
}

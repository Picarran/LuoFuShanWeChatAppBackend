package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class PostCommentReq {
    private Long postId;
    private String content;
}
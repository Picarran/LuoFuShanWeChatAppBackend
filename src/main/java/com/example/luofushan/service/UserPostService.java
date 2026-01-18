package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;

public interface UserPostService {

    UserPostResp createPost(UserPostReq req);

    Page<PostListResp> listPosts(PostListReq req);

    PostCommentResp addComment(PostCommentReq postCommentReq);

    Page<PostCommentListResp> listComments(PostCommentListReq req);

    PostLikeResp likePost(PostLikeReq req);
}
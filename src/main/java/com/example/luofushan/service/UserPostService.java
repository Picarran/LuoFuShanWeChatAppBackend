package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.req.PostCommentListReq;
import com.example.luofushan.dto.req.PostCommentReq;
import com.example.luofushan.dto.req.PostListReq;
import com.example.luofushan.dto.req.UserPostReq;
import com.example.luofushan.dto.resp.PostCommentListResp;
import com.example.luofushan.dto.resp.PostCommentResp;
import com.example.luofushan.dto.resp.PostListResp;
import com.example.luofushan.dto.resp.UserPostResp;

public interface UserPostService {

    UserPostResp createPost(UserPostReq req);

    Page<PostListResp> listPosts(PostListReq req);

    PostCommentResp addComment(PostCommentReq postCommentReq);

    Page<PostCommentListResp> listComments(PostCommentListReq req);
}
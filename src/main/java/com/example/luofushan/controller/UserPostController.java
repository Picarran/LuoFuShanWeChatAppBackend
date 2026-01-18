package com.example.luofushan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.service.UserPostService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class UserPostController {

    @Resource
    private UserPostService userPostService;

    @PostMapping("/user/share")
    public Result<UserPostResp> sharePost(@RequestBody UserPostReq req) {
        return Result.buildSuccess(userPostService.createPost(req));
    }

    @GetMapping("/list")
    public Result<Page<PostListResp>> listPosts(PostListReq req) {
        Page<PostListResp> page = userPostService.listPosts(req);
        return Result.buildSuccess(page);
    }

    @PostMapping("/comment")
    public Result<PostCommentResp> addComment(@RequestBody PostCommentReq req) {
        return Result.buildSuccess(userPostService.addComment(req));
    }

    @GetMapping("/comments")
    public Result<Page<PostCommentListResp>> getComments(PostCommentListReq req) {
        return Result.buildSuccess(userPostService.listComments(req));
    }

    @PostMapping("/like")
    public Result<PostLikeResp> like(@RequestBody PostLikeReq req) {
        return Result.buildSuccess(userPostService.likePost(req));
    }
}
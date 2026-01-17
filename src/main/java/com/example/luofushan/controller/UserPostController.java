package com.example.luofushan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.PostCommentListReq;
import com.example.luofushan.dto.req.PostCommentReq;
import com.example.luofushan.dto.req.PostListReq;
import com.example.luofushan.dto.req.UserPostReq;
import com.example.luofushan.dto.resp.PostCommentListResp;
import com.example.luofushan.dto.resp.PostCommentResp;
import com.example.luofushan.dto.resp.PostListResp;
import com.example.luofushan.dto.resp.UserPostResp;
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
}
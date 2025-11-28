package com.example.luofushan.controller;

import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.UserPostReq;
import com.example.luofushan.dto.resp.UserPostResp;
import com.example.luofushan.service.UserPostService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class UserPostController {

    @Resource
    private UserPostService userPostService;

    @PostMapping("/user/share")
    public Result<UserPostResp> sharePost(@RequestBody UserPostReq req) {
        return Result.buildSuccess(userPostService.createPost(req));
    }
}
package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.luofushan.dao.entity.UserPost;
import com.example.luofushan.dto.req.UserPostReq;
import com.example.luofushan.dto.resp.UserPostResp;

public interface UserPostService extends IService<UserPost> {

    UserPostResp createPost(UserPostReq req);
}
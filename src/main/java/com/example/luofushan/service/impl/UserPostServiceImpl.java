package com.example.luofushan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.luofushan.dao.entity.UserPost;
import com.example.luofushan.dao.mapper.UserPostMapper;
import com.example.luofushan.dto.req.UserPostReq;
import com.example.luofushan.dto.resp.UserPostResp;
import com.example.luofushan.service.UserPostService;
import org.springframework.stereotype.Service;

@Service
public class UserPostServiceImpl extends ServiceImpl<UserPostMapper, UserPost>
        implements UserPostService {

    @Override
    public UserPostResp createPost(UserPostReq req) {

        UserPost post = UserPost.builder()
                .userId(req.getUserId())
                .locationId(req.getLocationId())
                .content(req.getContent())
                .images(req.getImages())
                .likeCount(0)
                .commentCount(0)
                .postTime(req.getPostTime())
                .build();

        this.save(post);


        return BeanUtil.toBean(post, UserPostResp.class);
    }
}

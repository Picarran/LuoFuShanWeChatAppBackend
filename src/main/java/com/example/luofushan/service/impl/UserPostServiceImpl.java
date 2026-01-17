package com.example.luofushan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.PostComment;
import com.example.luofushan.dao.entity.UserPost;
import com.example.luofushan.dao.mapper.CheckinLocationMapper;
import com.example.luofushan.dao.mapper.PostCommentMapper;
import com.example.luofushan.dao.mapper.UserPostMapper;
import com.example.luofushan.dto.req.PostCommentListReq;
import com.example.luofushan.dto.req.PostCommentReq;
import com.example.luofushan.dto.req.PostListReq;
import com.example.luofushan.dto.req.UserPostReq;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.security.UserContext;
import com.example.luofushan.service.UserPostService;
import jakarta.annotation.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPostServiceImpl implements UserPostService {
    @Resource
    private UserPostMapper userPostMapper;
    @Resource
    private CheckinLocationMapper locationMapper;
    @Resource
    private PostCommentMapper postCommentMapper;

    @Override
    public UserPostResp createPost(UserPostReq req) {

        UserPost post = UserPost.builder()
                .userId(UserContext.getUserId())
                .locationId(req.getLocationId())
                .content(req.getContent())
                .images(req.getImages())
                .likeCount(0)
                .commentCount(0)
                .postTime(req.getPostTime())
                .build();

        userPostMapper.insert(post);

        return BeanUtil.toBean(post, UserPostResp.class);
    }

    @Override
    public Page<PostListRealResp> listPosts(PostListReq req) {
        req.initDefault();
        int offset = (req.getPage() - 1) * req.getSize();

        if("distance".equals(req.getSortBy()) && (req.getLatitude()==null || req.getLongitude()==null)) {
            throw LuoFuShanException.hasNoDistanceInfo();
        }

        List<PostListResp> originRecords = userPostMapper.selectPosts(req.getFuzzy(), req.getLatitude(), req.getLongitude(), offset, req.getSize(), req.getSortBy());
        int total = userPostMapper.countPosts(req.getFuzzy());

        // ==== 转换为最终返回的 PostListRealResp ====
        List<PostListRealResp> realList = originRecords.stream()
                .map(item -> {
                    // 1. 复制除 images 外的所有字段
                    PostListRealResp real = BeanUtil.toBean(item, PostListRealResp.class);

                    // 2. 解析 imagesStr → List<String>
                    if (item.getImagesStr() != null) {
                        try {
                            real.setImages(JSON.parseArray(item.getImagesStr(), String.class));
                        } catch (Exception e) {
                            // JSON 非法时防止抛错（例如 null 或非 JSON 格式）
                            real.setImages(Collections.emptyList());
                        }
                    } else {
                        real.setImages(Collections.emptyList());
                    }

                    return real;
                })
                .collect(Collectors.toList());

        // 拼 Page（保持你现有的 Page 结构）
        Page<PostListRealResp> page = new Page<>(req.getPage(), req.getSize());
        page.setTotal(total);
        page.setRecords(realList);

        return page;
    }

    @Override
    @Transactional
    public PostCommentResp addComment(PostCommentReq postCommentReq) {
        PostComment comment = BeanUtil.toBean(postCommentReq, PostComment.class);
        comment.setUserId(UserContext.getUserId());
        try {
            postCommentMapper.insert(comment);

            // 更新count
            LambdaQueryWrapper<UserPost> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserPost::getDelflag, 0)
                    .eq(UserPost::getId, postCommentReq.getPostId());
            UserPost up = userPostMapper.selectOne(wrapper);
            up.setCommentCount(up.getCommentCount() + 1);
            userPostMapper.updateById(up);
        }catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw LuoFuShanException.UserOrPostNotExists();
        }
        return BeanUtil.toBean(comment, PostCommentResp.class);
    }

    @Override
    public Page<PostCommentListResp> listComments(PostCommentListReq req) {
        req.initDefault();
        int offset = (req.getPage() - 1) * req.getSize();
        List<PostCommentListResp> records =  postCommentMapper.selectCommentList(req.getPostId(), offset, req.getSize());
        int total = postCommentMapper.countByPostId(req.getPostId());

        Page<PostCommentListResp> page = new Page<>(req.getPage(), req.getSize(), total);
        page.setRecords(records);
        page.setPages((total + req.getSize() - 1) / req.getSize());
        return page;
    }
}

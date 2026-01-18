package com.example.luofushan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.PostComment;
import com.example.luofushan.dao.entity.UserPost;
import com.example.luofushan.dao.mapper.CheckinLocationMapper;
import com.example.luofushan.dao.mapper.PostCommentMapper;
import com.example.luofushan.dao.mapper.UserPostMapper;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.security.UserContext;
import com.example.luofushan.service.UserPostService;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.luofushan.common.constant.RedisCacheConstant.POST_LIKE_KEY;

@Service
public class UserPostServiceImpl implements UserPostService {
    @Resource
    private UserPostMapper userPostMapper;
    @Resource
    private CheckinLocationMapper locationMapper;
    @Resource
    private PostCommentMapper postCommentMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
    public Page<PostListResp> listPosts(PostListReq req) {
        req.initDefault();

        Long uid = UserContext.getUserId();
        if(uid==null) throw new LuoFuShanException("未登录");


        int offset = (req.getPage() - 1) * req.getSize();

        if("distance".equals(req.getSortBy()) && (req.getLatitude()==null || req.getLongitude()==null)) {
            throw LuoFuShanException.hasNoDistanceInfo();
        }

        List<PostListResp> records = userPostMapper.selectPosts(req.getFuzzy(), req.getLatitude(), req.getLongitude(), offset, req.getSize(), req.getSortBy());
        int total = userPostMapper.countPosts(req.getFuzzy());

        records.forEach(post -> {
            String key = POST_LIKE_KEY + uid + "+" + post.getId();
            boolean liked = stringRedisTemplate.hasKey(key);
            post.setLiked(liked);
        });

        Page<PostListResp> page = new Page<>(req.getPage(),req.getSize(),total);
        page.setTotal(total);
        page.setRecords(records);

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

    @Override
    public PostLikeResp likePost(PostLikeReq req) {
        if(req.getPostId()==null || StringUtil.isNullOrEmpty(req.getAction())) {
            throw new LuoFuShanException("postId或action为空");
        }
        if(!req.getAction().equals("like") && !req.getAction().equals("unlike")){
            throw new LuoFuShanException("action必须为like或unlike");
        }
        LambdaQueryWrapper<UserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPost::getDelflag, 0)
                .eq(UserPost::getId, req.getPostId());
        UserPost userPost = userPostMapper.selectOne(wrapper);
        if(userPost==null) throw new LuoFuShanException("动态不存在");

        Long uid = UserContext.getUserId();
        Long pid = req.getPostId();
        String key = POST_LIKE_KEY + uid + "+" + pid;

        if(req.getAction().equals("like")) {
            if(!stringRedisTemplate.hasKey(key)) {
                stringRedisTemplate.opsForValue().set(key, "", 30, TimeUnit.DAYS);
                userPost.setLikeCount(userPost.getLikeCount() + 1);
                userPostMapper.updateById(userPost);
            }
            return PostLikeResp.builder()
                    .liked(true)
                    .postId(pid)
                    .likeCount(userPost.getLikeCount())
                    .build();
        } else {
            if(stringRedisTemplate.hasKey(key)) {
                stringRedisTemplate.delete(key);
                userPost.setLikeCount(userPost.getLikeCount() - 1);
                userPostMapper.updateById(userPost);
            }
            return PostLikeResp.builder()
                    .liked(false)
                    .postId(pid)
                    .likeCount(userPost.getLikeCount())
                    .build();
        }
    }
}

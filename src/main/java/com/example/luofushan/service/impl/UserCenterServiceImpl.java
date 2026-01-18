package com.example.luofushan.service.impl;

import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.User;
import com.example.luofushan.dao.mapper.UserMapper;
import com.example.luofushan.dto.req.UpdateUserProfileReq;
import com.example.luofushan.dto.resp.UserProfileResp;
import com.example.luofushan.security.UserContext;
import com.example.luofushan.service.CheckinService;
import com.example.luofushan.service.UserCenterService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserCenterServiceImpl implements UserCenterService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private CheckinService checkinService;

    @Override
    public UserProfileResp getProfile() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            // 正常情况下被 TokenFilter 拦截，这里兜底
            throw new LuoFuShanException("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LuoFuShanException("用户不存在");
        }

        UserProfileResp resp = new UserProfileResp();
        resp.setId(user.getId());
        resp.setNickname(user.getNickname());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setPoints(user.getPoints());
        resp.setWeeklyCheckinCount(checkinService.getUserCheckinWeekCount(userId));
        resp.setDaylyCheckinCount(checkinService.getUserCheckinDayCount(userId));
        resp.setMonthlyCheckinCount(checkinService.getUserCheckinMonthCount(userId));
        return resp;
    }

    @Override
    public UserProfileResp updateProfile(UpdateUserProfileReq req) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new LuoFuShanException("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LuoFuShanException("用户不存在");
        }

        boolean changed = false;
        if (StringUtils.hasText(req.getNickname())) {
            user.setNickname(req.getNickname());
            changed = true;
        }
        if (StringUtils.hasText(req.getAvatarUrl())) {
            user.setAvatarUrl(req.getAvatarUrl());
            changed = true;
        }
        if (!changed) {
            throw new LuoFuShanException("至少传入一个要修改的字段");
        }

        userMapper.updateById(user);

        UserProfileResp resp = new UserProfileResp();
        resp.setId(user.getId());
        resp.setNickname(user.getNickname());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setPoints(user.getPoints());
        resp.setWeeklyCheckinCount(checkinService.getUserCheckinWeekCount(userId));
        resp.setDaylyCheckinCount(checkinService.getUserCheckinDayCount(userId));
        resp.setMonthlyCheckinCount(checkinService.getUserCheckinMonthCount(userId));
        return resp;
    }
}

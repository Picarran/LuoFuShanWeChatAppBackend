package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.CheckinLocation;
import com.example.luofushan.dao.entity.User;
import com.example.luofushan.dao.entity.UserCheckin;
import com.example.luofushan.dao.mapper.CheckinLocationMapper;
import com.example.luofushan.dao.mapper.UserCheckinMapper;
import com.example.luofushan.dao.mapper.UserMapper;
import com.example.luofushan.dto.req.UserCheckinHistoryReq;
import com.example.luofushan.dto.req.UserCheckinReq;
import com.example.luofushan.dto.resp.CheckinLocationListResp;
import com.example.luofushan.dto.resp.UserCheckinHistoryResp;
import com.example.luofushan.dto.resp.UserCheckinResp;
import com.example.luofushan.security.UserContext;
import com.example.luofushan.service.CheckinService;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckinServiceImpl implements CheckinService {

    @Resource
    private CheckinLocationMapper checkinLocationMapper;
    @Resource
    private UserCheckinMapper userCheckinMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public List<CheckinLocationListResp> getAllLocations() {
        // 查询所有未逻辑删除的打卡点
        LambdaQueryWrapper<CheckinLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinLocation::getDelflag, 0);

        List<CheckinLocation> locations = checkinLocationMapper.selectList(wrapper);

        return locations.stream().map(loc -> CheckinLocationListResp.builder()
                .id(loc.getId())
                .name(loc.getName())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .score(loc.getScore())
                .todayHasCheckin(loc.getTodayHasCheckin())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public Page<UserCheckinHistoryResp> getUserHistory(UserCheckinHistoryReq req) {
        req.initDefault();
        int offset = (req.getPage() - 1) * req.getSize();
        List<UserCheckinHistoryResp> records = userCheckinMapper.selectHistoryByUserId(UserContext.getUserId(), offset, req.getSize());
        int total = userCheckinMapper.countByUserId(UserContext.getUserId());

        Page<UserCheckinHistoryResp> page = new Page<>(req.getPage(), req.getSize());
        page.setRecords(records);
        page.setTotal(total);
        page.setPages((total + req.getSize() - 1) / req.getSize());

        return page;
    }

    @Override
    @Transactional
    public UserCheckinResp doUserCheckin(UserCheckinReq req) {

        // 1. 校验景点是否存在, 校验用户
        CheckinLocation loc = checkinLocationMapper.selectById(req.getLocationId());
        if (loc == null) {
            throw LuoFuShanException.checkinLocationNotExists();
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new LuoFuShanException("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LuoFuShanException("用户不存在");
        }

        // 2. 插入打卡记录（利用 UNIQUE KEY 防重复）
        UserCheckin uc = UserCheckin.builder()
                .userId(UserContext.getUserId())
                .locationId(req.getLocationId())
                .checkinTime(req.getCheckinTime())
                .build();

        try {
            userCheckinMapper.insert(uc);
        } catch (DuplicateKeyException e) {
            throw LuoFuShanException.alreadyHit();
        }

        // 3. 景点今日打卡数 + 1
        checkinLocationMapper.incrementTodayCount(req.getLocationId());

        // 4. 获取更新后的今日打卡数
        Long todayCount = checkinLocationMapper.selectTodayCount(req.getLocationId());

        // 5. 更新用户积分
        int score = loc.getScore();
        user.setPoints(user.getPoints() + score);
        userMapper.updateById(user);

        // 6. 构造返回对象
        return UserCheckinResp.builder()
                .id(uc.getId())
                .locationId(loc.getId())
                .locationName(loc.getName())
                .checkinTime(req.getCheckinTime())
                .score(loc.getScore())
                .todayHasCheckin(todayCount)
                .build();
    }
}
package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dao.entity.CheckinLocation;
import com.example.luofushan.dao.mapper.CheckinLocationMapper;
import com.example.luofushan.dao.mapper.UserCheckinMapper;
import com.example.luofushan.dto.req.UserCheckinHistoryReq;
import com.example.luofushan.dto.resp.CheckinLocationListResp;
import com.example.luofushan.dto.resp.UserCheckinHistoryResp;
import com.example.luofushan.service.CheckinService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckinServiceImpl implements CheckinService {

    @Resource
    private CheckinLocationMapper checkinLocationMapper;
    @Resource
    private UserCheckinMapper userCheckinMapper;

    @Override
    public List<CheckinLocationListResp> getAllLocations() {
        // 查询所有未逻辑删除的打卡点
        LambdaQueryWrapper<CheckinLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinLocation::getDelflag, 0);

        List<CheckinLocation> locations = checkinLocationMapper.selectList(wrapper);

        return locations.stream().map(loc -> CheckinLocationListResp.builder()
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
        List<UserCheckinHistoryResp> records = userCheckinMapper.selectHistoryByUserId(req.getUserId(), offset, req.getSize());
        int total = userCheckinMapper.countByUserId(req.getUserId());

        Page<UserCheckinHistoryResp> page = new Page<>(req.getPage(), req.getSize());
        page.setRecords(records);
        page.setTotal(total);
        page.setPages((total + req.getSize() - 1) / req.getSize());

        return page;
    }
}
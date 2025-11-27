package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.luofushan.dao.entity.CheckinLocation;
import com.example.luofushan.dao.mapper.CheckinLocationMapper;
import com.example.luofushan.dto.resp.CheckinLocationListResp;
import com.example.luofushan.service.CheckinLocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckinLocationServiceImpl extends ServiceImpl<CheckinLocationMapper, CheckinLocation>
        implements CheckinLocationService {

    @Override
    public List<CheckinLocationListResp> getAllLocations() {
        // 查询所有未逻辑删除的打卡点
        LambdaQueryWrapper<CheckinLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinLocation::getDelflag, 0);

        List<CheckinLocation> locations = this.list(wrapper);

        return locations.stream().map(loc -> CheckinLocationListResp.builder()
                .name(loc.getName())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .score(loc.getScore())
                .todayHasCheckin(loc.getTodayHasCheckin())
                .build()
        ).collect(Collectors.toList());
    }
}
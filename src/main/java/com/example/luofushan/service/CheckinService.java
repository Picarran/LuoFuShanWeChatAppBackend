package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.req.CheckinRankPageReq;
import com.example.luofushan.dto.req.UserCheckinHistoryReq;
import com.example.luofushan.dto.req.UserCheckinReq;
import com.example.luofushan.dto.resp.*;

import java.util.List;

public interface CheckinService {

    List<CheckinLocationListResp> getAllLocations();
    Page<UserCheckinHistoryResp> getUserHistory(UserCheckinHistoryReq req);

    UserCheckinResp doUserCheckin(UserCheckinReq req);

    long getUserCheckinMonthCount(Long id);

    long getUserCheckinWeekCount(Long id);

    long getUserCheckinDayCount(Long id);

    long getLocationCheckinCount(Long id);

    Page<CheckinRankPageResp> getRank(CheckinRankPageReq req);

    CheckinRankMeResp getRankMe(String type);
}
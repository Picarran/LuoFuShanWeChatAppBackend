package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.req.UserCheckinHistoryReq;
import com.example.luofushan.dto.req.UserCheckinReq;
import com.example.luofushan.dto.resp.CheckinLocationListResp;
import com.example.luofushan.dto.resp.UserCheckinHistoryResp;
import com.example.luofushan.dto.resp.UserCheckinResp;

import java.util.List;

public interface CheckinService {

    List<CheckinLocationListResp> getAllLocations();
    Page<UserCheckinHistoryResp> getUserHistory(UserCheckinHistoryReq req);

    UserCheckinResp doUserCheckin(UserCheckinReq req);
}
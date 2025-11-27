package com.example.luofushan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.UserCheckinHistoryReq;
import com.example.luofushan.dto.resp.CheckinLocationListResp;
import com.example.luofushan.dto.resp.UserCheckinHistoryResp;
import com.example.luofushan.service.CheckinService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
public class CheckinLocationController {

    @Resource
    private CheckinService checkinService;

    @GetMapping("/location/list")
    public Result<List<CheckinLocationListResp>> getAllLocations() {
        List<CheckinLocationListResp> locations = checkinService.getAllLocations();
        return Result.buildSuccess(locations);
    }

    @GetMapping("/user/history")
    public Result<Page<UserCheckinHistoryResp>> getUserHistory(UserCheckinHistoryReq req) {

        Page<UserCheckinHistoryResp> historyPage = checkinService.getUserHistory(req);
        return Result.buildSuccess(historyPage);
    }
}
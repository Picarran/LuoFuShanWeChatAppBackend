package com.example.luofushan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.CheckinRankPageReq;
import com.example.luofushan.dto.req.UserCheckinHistoryReq;
import com.example.luofushan.dto.req.UserCheckinReq;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.service.CheckinService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkin")
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

    @PostMapping("/user")
    public Result<UserCheckinResp> userCheckin(UserCheckinReq req) {
        return Result.buildSuccess(checkinService.doUserCheckin(req));
    }

    @GetMapping("/rank")
    public Result<Page<CheckinRankPageResp>> getRank(CheckinRankPageReq req) {
        return Result.buildSuccess(checkinService.getRank(req));
    }

    @GetMapping("/rank/me")
    public Result<CheckinRankMeResp> getRankMe(@RequestParam String type) {
        return Result.buildSuccess(checkinService.getRankMe(type));
    }
}
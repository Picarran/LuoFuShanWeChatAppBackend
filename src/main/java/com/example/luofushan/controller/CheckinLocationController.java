package com.example.luofushan.controller;

import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.resp.CheckinLocationListResp;
import com.example.luofushan.service.CheckinLocationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CheckinLocationController {

    @Resource
    private CheckinLocationService checkinLocationService;


    @GetMapping("/checkin/location/list")
    public Result<List<CheckinLocationListResp>> getAllLocations() {
        List<CheckinLocationListResp> locations = checkinLocationService.getAllLocations();
        return Result.buildSuccess(locations);
    }
}
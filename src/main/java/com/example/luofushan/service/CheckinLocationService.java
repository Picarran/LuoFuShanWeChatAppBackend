package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.luofushan.dao.entity.CheckinLocation;
import com.example.luofushan.dto.resp.CheckinLocationListResp;

import java.util.List;

public interface CheckinLocationService extends IService<CheckinLocation> {
    List<CheckinLocationListResp> getAllLocations();
}
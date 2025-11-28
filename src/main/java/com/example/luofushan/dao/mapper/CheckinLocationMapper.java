package com.example.luofushan.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.luofushan.dao.entity.CheckinLocation;
import org.apache.ibatis.annotations.Param;

public interface CheckinLocationMapper extends BaseMapper<CheckinLocation> {

    void incrementTodayCount(@Param("id") Long id);

    Long selectTodayCount(@Param("id") Long id);
}
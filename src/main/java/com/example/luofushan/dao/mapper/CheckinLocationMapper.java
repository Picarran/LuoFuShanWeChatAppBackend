package com.example.luofushan.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.luofushan.dao.entity.CheckinLocation;
import org.apache.ibatis.annotations.Param;

public interface CheckinLocationMapper extends BaseMapper<CheckinLocation> {

    @Deprecated
    void incrementTodayCount(@Param("id") Long id);

    @Deprecated
    Long selectTodayCount(@Param("id") Long id);
}
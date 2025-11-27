package com.example.luofushan.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.luofushan.dao.entity.UserCheckin;
import com.example.luofushan.dto.resp.UserCheckinHistoryResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCheckinMapper extends BaseMapper<UserCheckin> {

    List<UserCheckinHistoryResp> selectHistoryByUserId(@Param("userId") Long userId,
                                                       @Param("offset") int offset,
                                                       @Param("size") int size);

    int countByUserId(@Param("userId") Long userId);
}
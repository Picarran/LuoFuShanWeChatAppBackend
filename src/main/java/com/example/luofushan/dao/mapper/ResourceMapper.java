package com.example.luofushan.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.luofushan.dao.entity.Resource;
import com.example.luofushan.dto.resp.NearbyResourceResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceMapper extends BaseMapper<Resource> {

    List<NearbyResourceResp> selectNearbyResources(
            @Param("type") String type,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("sortBy") String sortBy
    );

    int countNearbyResources(@Param("type") String type);
}

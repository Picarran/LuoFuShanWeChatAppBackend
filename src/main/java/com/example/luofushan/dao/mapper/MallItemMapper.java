package com.example.luofushan.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.luofushan.dao.entity.MallItem;
import com.example.luofushan.dto.resp.MallItemListResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallItemMapper extends BaseMapper<MallItem> {

    List<MallItemListResp> selectMallItemList(
            @Param("sort") String sort,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int countMallItems();
}

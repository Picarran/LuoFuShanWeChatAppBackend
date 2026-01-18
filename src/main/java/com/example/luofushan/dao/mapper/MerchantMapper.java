package com.example.luofushan.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.luofushan.dao.entity.Merchant;
import com.example.luofushan.dto.resp.AdminMerchantListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    List<AdminMerchantListResp> selectMerchantPage(
            @Param("type") String type,
            @Param("fuzzy") String fuzzy,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("status") int status
    );

    int countMerchant(
            @Param("type") String type,
            @Param("fuzzy") String fuzzy,
            @Param("status") int status
    );
}
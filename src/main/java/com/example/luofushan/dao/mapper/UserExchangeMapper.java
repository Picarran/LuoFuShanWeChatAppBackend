package com.example.luofushan.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.luofushan.dao.entity.UserExchange;
import com.example.luofushan.dto.resp.MyExchangeRecordResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserExchangeMapper extends BaseMapper<UserExchange> {

    List<MyExchangeRecordResp> selectMyExchangeList(
            @Param("userId") Long userId,
            @Param("isRedeemed") Integer isRedeemed,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int countMyExchange(
            @Param("userId") Long userId,
            @Param("isRedeemed") Integer isRedeemed
    );
}

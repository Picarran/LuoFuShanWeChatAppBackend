package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.MallItem;
import com.example.luofushan.dao.entity.User;
import com.example.luofushan.dao.entity.UserExchange;
import com.example.luofushan.dao.mapper.MallItemMapper;
import com.example.luofushan.dao.mapper.UserExchangeMapper;
import com.example.luofushan.dao.mapper.UserMapper;
import com.example.luofushan.dto.req.ExchangeItemReq;
import com.example.luofushan.dto.req.MallItemListReq;
import com.example.luofushan.dto.req.MyExchangeListReq;
import com.example.luofushan.dto.resp.ExchangeResultResp;
import com.example.luofushan.dto.resp.MallItemListResp;
import com.example.luofushan.dto.resp.MyExchangeRecordResp;
import com.example.luofushan.security.UserContext;
import com.example.luofushan.service.ExchangeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    @Resource
    private UserExchangeMapper userExchangeMapper;

    @Resource
    private MallItemMapper mallItemMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<MyExchangeRecordResp> getMyExchangeList(MyExchangeListReq req) {
        req.initDefault();

        Integer isRedeemed;
        if ("redeemed".equals(req.getStatus())) {
            isRedeemed = 1;
        } else if ("unredeemed".equals(req.getStatus())) {
            isRedeemed = 0;
        } else {
            throw LuoFuShanException.invalidStatus();
        }

        Long userId = UserContext.getUserId();
        int offset = (req.getCurrent() - 1) * req.getSize();

        List<MyExchangeRecordResp> records = userExchangeMapper.selectMyExchangeList(
                userId, isRedeemed, offset, req.getSize()
        );

        records.forEach(record -> record.setIsRedeemed(record.getIsRedeemed() != null && record.getIsRedeemed()));

        int total = userExchangeMapper.countMyExchange(userId, isRedeemed);

        Page<MyExchangeRecordResp> page = new Page<>(req.getCurrent(), req.getSize(), total);
        page.setRecords(records);
        page.setPages((total + req.getSize() - 1) / req.getSize());

        return page;
    }

    @Override
    public Page<MallItemListResp> getMallItemList(MallItemListReq req) {
        req.initDefault();

        int offset = (req.getCurrent() - 1) * req.getSize();

        List<MallItemListResp> records = mallItemMapper.selectMallItemList(
                req.getSort(), offset, req.getSize()
        );

        int total = mallItemMapper.countMallItems();

        Page<MallItemListResp> page = new Page<>(req.getCurrent(), req.getSize(), total);
        page.setRecords(records);
        page.setPages((total + req.getSize() - 1) / req.getSize());

        return page;
    }

    @Override
    @Transactional
    public ExchangeResultResp exchangeItem(ExchangeItemReq req) {
        Long userId = UserContext.getUserId();

        MallItem item = mallItemMapper.selectById(req.getItemId());
        if (item == null || item.getDelflag() == 1) {
            throw LuoFuShanException.mallItemNotExists();
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LuoFuShanException("用户不存在");
        }

        if (user.getPoints() < item.getPointsCost()) {
            throw LuoFuShanException.pointsNotEnough();
        }

        user.setPoints(user.getPoints() - item.getPointsCost());
        userMapper.updateById(user);

        String code = generateExchangeCode();

        UserExchange exchange = UserExchange.builder()
                .userId(userId)
                .itemId(item.getId())
                .code(code)
                .isRedeemed(0)
                .placeName("待指定兑换地点")
                .build();

        userExchangeMapper.insert(exchange);

        return ExchangeResultResp.builder()
                .recordId(exchange.getId())
                .itemId(item.getId())
                .itemName(item.getName())
                .code(code)
                .isRedeemed(false)
                .placeName(exchange.getPlaceName())
                .createdAt(exchange.getCreatedAt())
                .build();
    }

    /**
     * 生成兑换卡密（格式：XXXX-XXXX-XXXX）
     */
    private String generateExchangeCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return uuid.substring(0, 4) + "-" + uuid.substring(4, 8) + "-" + uuid.substring(8, 12);
    }
}

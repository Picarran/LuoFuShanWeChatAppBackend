package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.Merchant;
import com.example.luofushan.dao.entity.MerchantToken;
import com.example.luofushan.dao.entity.Resource;
import com.example.luofushan.dao.entity.User;
import com.example.luofushan.dao.entity.UserExchange;
import com.example.luofushan.dao.mapper.*;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.security.MerchantContext;
import com.example.luofushan.service.MerchantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;
    private final MerchantTokenMapper merchantTokenMapper;
    private final ResourceMapper resourceMapper;
    private final MallItemMapper mallItemMapper;
    private final UserExchangeMapper userExchangeMapper;
    private final UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MerchantLoginResp login(MerchantLoginReq req) {
        if (!StringUtils.hasText(req.getUsername()) || !StringUtils.hasText(req.getPassword())) {
            throw new LuoFuShanException("用户名和密码不能为空");
        }

        Merchant merchant = merchantMapper.selectOne(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getUsername, req.getUsername())
        );
        if (merchant == null) {
            throw new LuoFuShanException("商家账号不存在");
        }
        if (merchant.getStatus() != null && merchant.getStatus() == 0) {
            throw new LuoFuShanException("商家账号已被禁用");
        }

        String inputHash = DigestUtils.md5DigestAsHex(req.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!inputHash.equals(merchant.getPassword())) {
            throw new LuoFuShanException("密码错误");
        }

        // 生成 token,默认 7 天
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

        // 清理旧 token
        merchantTokenMapper.delete(
                new LambdaQueryWrapper<MerchantToken>().eq(MerchantToken::getMerchantId, merchant.getId())
        );

        MerchantToken mt = new MerchantToken();
        mt.setMerchantId(merchant.getId());
        mt.setToken(token);
        mt.setExpireAt(expiredAt);
        merchantTokenMapper.insert(mt);

        MerchantLoginResp resp = new MerchantLoginResp();
        resp.setMerchantId(merchant.getId());
        resp.setName(merchant.getName());
        resp.setType(merchant.getType());
        resp.setToken(token);
        resp.setExpiredAt(expiredAt);
        return resp;
    }

    @Override
    public MerchantProfileResp getProfile() {
        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new LuoFuShanException("未登录商家");
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new LuoFuShanException("商家不存在");
        }

        MerchantProfileResp resp = new MerchantProfileResp();
        resp.setMerchantId(merchant.getId());
        resp.setMerchantName(merchant.getName());
        resp.setMerchantType(merchant.getType());
        resp.setResourceId(merchant.getResourceId());
        resp.setStatus(merchant.getStatus());
        resp.setCreatedAt(merchant.getCreatedAt());
        return resp;
    }

    @Override
    public MerchantResourceDetailResp getResourceDetail() {
        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new LuoFuShanException("未登录商家");
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new LuoFuShanException("商家不存在");
        }
        if (merchant.getResourceId() == null) {
            throw new LuoFuShanException("商家未绑定资源");
        }

        Resource resource = resourceMapper.selectById(merchant.getResourceId());
        if (resource == null) {
            throw LuoFuShanException.resourceNotExists();
        }

        MerchantResourceDetailResp resp = new MerchantResourceDetailResp();
        resp.setResourceId(resource.getId());
        resp.setName(resource.getName());
        resp.setType(resource.getType());
        resp.setCoverImg(resource.getCoverImg());
        resp.setLatitude(resource.getLatitude());
        resp.setLongitude(resource.getLongitude());
        resp.setHotScore(resource.getHotScore());

        String contentJson = resource.getContentJson();
        if (StringUtils.hasText(contentJson)) {
            try {
                List<MerchantResourceContentBlock> list = objectMapper.readValue(
                        contentJson,
                        objectMapper.getTypeFactory().constructCollectionType(
                                List.class, MerchantResourceContentBlock.class)
                );
                resp.setContent(list);
            } catch (Exception e) {
                // 若解析失败，返回空列表，避免前端崩溃
                resp.setContent(Collections.emptyList());
            }
        } else {
            resp.setContent(Collections.emptyList());
        }
        return resp;
    }

    @Override
    public void updateResourceBase(MerchantResourceBaseUpdateReq req) {
        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new LuoFuShanException("未登录商家");
        }
        if (!StringUtils.hasText(req.getName())) {
            throw new LuoFuShanException("名称不能为空");
        }

        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new LuoFuShanException("商家不存在");
        }
        if (merchant.getResourceId() == null) {
            throw new LuoFuShanException("商家未绑定资源");
        }

        Resource resource = resourceMapper.selectById(merchant.getResourceId());
        if (resource == null) {
            throw LuoFuShanException.resourceNotExists();
        }

        resource.setName(req.getName());
        if (StringUtils.hasText(req.getCoverImg())) {
            resource.setCoverImg(req.getCoverImg());
        }
        if (req.getLatitude() != null) {
            resource.setLatitude(req.getLatitude());
        }
        if (req.getLongitude() != null) {
            resource.setLongitude(req.getLongitude());
        }

        resourceMapper.updateById(resource);
    }

    @Override
    public void updateResourceContent(MerchantResourceContentUpdateReq req) {
        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new LuoFuShanException("未登录商家");
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new LuoFuShanException("商家不存在");
        }
        if (merchant.getResourceId() == null) {
            throw new LuoFuShanException("商家未绑定资源");
        }

        Resource resource = resourceMapper.selectById(merchant.getResourceId());
        if (resource == null) {
            throw LuoFuShanException.resourceNotExists();
        }

        try {
            String json = objectMapper.writeValueAsString(req.getContent());
            resource.setContentJson(json);
            resourceMapper.updateById(resource);
        } catch (Exception e) {
            throw new LuoFuShanException("内容结构序列化失败");
        }
    }

    @Override
    public Page<MallItemListResp> getMallItemList(MerchantMallItemListReq req) {
        req.initDefault();

        int offset = (req.getPage() - 1) * req.getSize();

        List<MallItemListResp> records = mallItemMapper.selectMallItemList(
                req.getSort(), offset, req.getSize()
        );

        int total = mallItemMapper.countMallItems();

        Page<MallItemListResp> page = new Page<>(req.getPage(), req.getSize(), total);
        page.setRecords(records);
        page.setPages((total + req.getSize() - 1) / req.getSize());

        return page;
    }

    @Override
    public Page<MerchantExchangeRecordResp> getExchangeList(MerchantExchangeListReq req) {
        req.initDefault();

        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new LuoFuShanException("未登录商家");
        }

        Integer isRedeemed;
        if ("redeemed".equals(req.getStatus())) {
            isRedeemed = 1;
        } else if ("unredeemed".equals(req.getStatus())) {
            isRedeemed = 0;
        } else {
            throw LuoFuShanException.invalidStatus();
        }

        int offset = (req.getPage() - 1) * req.getSize();

        List<MerchantExchangeRecordResp> records = userExchangeMapper.selectMerchantExchangeList(
                merchantId, isRedeemed, offset, req.getSize()
        );

        records.forEach(record -> record.setIsRedeemed(record.getIsRedeemed() != null && record.getIsRedeemed()));

        int total = userExchangeMapper.countMerchantExchange(merchantId, isRedeemed);

        Page<MerchantExchangeRecordResp> page = new Page<>(req.getPage(), req.getSize(), total);
        page.setRecords(records);
        page.setPages((total + req.getSize() - 1) / req.getSize());

        return page;
    }

    @Override
    @Transactional
    public MerchantRedeemResp redeemCode(MerchantRedeemReq req) {
        if (!StringUtils.hasText(req.getCode())) {
            throw new LuoFuShanException("兑换码不能为空");
        }

        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new LuoFuShanException("未登录商家");
        }

        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new LuoFuShanException("商家不存在");
        }

        // 查询兑换记录
        UserExchange exchange = userExchangeMapper.selectOne(
                new LambdaQueryWrapper<UserExchange>()
                        .eq(UserExchange::getCode, req.getCode())
                        .eq(UserExchange::getDelflag, 0)
        );

        if (exchange == null) {
            throw new LuoFuShanException("兑换码不存在");
        }

        if (exchange.getIsRedeemed() != null && exchange.getIsRedeemed() == 1) {
            throw new LuoFuShanException("兑换码已被核销");
        }

        // 更新核销状态
        LocalDateTime now = LocalDateTime.now();
        exchange.setIsRedeemed(1);
        exchange.setRedeemedAt(now);
        exchange.setMerchantId(merchantId);
        exchange.setPlaceName(merchant.getName());
        userExchangeMapper.updateById(exchange);

        // 查询商品名称
        String itemName = "";
        if (exchange.getItemId() != null) {
            var item = mallItemMapper.selectById(exchange.getItemId());
            if (item != null) {
                itemName = item.getName();
            }
        }

        MerchantRedeemResp resp = new MerchantRedeemResp();
        resp.setExchangeId(exchange.getId());
        resp.setItemName(itemName);
        resp.setPlaceName(merchant.getName());
        resp.setRedeemedAt(now);
        return resp;
    }

    @Override
    public MerchantUserPointsResp getUserPoints(MerchantUserPointsReq req) {
        if (req.getUserId() == null) {
            throw new LuoFuShanException("用户ID不能为空");
        }

        User user = userMapper.selectById(req.getUserId());
        if (user == null) {
            throw new LuoFuShanException("用户不存在");
        }

        MerchantUserPointsResp resp = new MerchantUserPointsResp();
        resp.setUserId(user.getId());
        resp.setPoints(user.getPoints());
        return resp;
    }

    @Override
    @Transactional
    public MerchantUserPointsResp updateUserPoints(MerchantPointsUpdateReq req) {
        if (req.getUserId() == null) {
            throw new LuoFuShanException("用户ID不能为空");
        }
        if (req.getDelta() == null) {
            throw new LuoFuShanException("积分变更值不能为空");
        }

        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new LuoFuShanException("未登录商家");
        }

        User user = userMapper.selectById(req.getUserId());
        if (user == null) {
            throw new LuoFuShanException("用户不存在");
        }

        int newPoints = user.getPoints() + req.getDelta();
        if (newPoints < 0) {
            throw new LuoFuShanException("积分不足，无法扣减");
        }

        user.setPoints(newPoints);
        userMapper.updateById(user);

        MerchantUserPointsResp resp = new MerchantUserPointsResp();
        resp.setUserId(user.getId());
        resp.setPoints(newPoints);
        return resp;
    }
}
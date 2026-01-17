package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.Merchant;
import com.example.luofushan.dao.entity.MerchantToken;
import com.example.luofushan.dao.entity.Resource;
import com.example.luofushan.dao.mapper.MerchantMapper;
import com.example.luofushan.dao.mapper.MerchantTokenMapper;
import com.example.luofushan.dao.mapper.ResourceMapper;
import com.example.luofushan.dto.req.MerchantLoginReq;
import com.example.luofushan.dto.req.MerchantResourceBaseUpdateReq;
import com.example.luofushan.dto.req.MerchantResourceContentUpdateReq;
import com.example.luofushan.dto.resp.MerchantLoginResp;
import com.example.luofushan.dto.resp.MerchantProfileResp;
import com.example.luofushan.dto.resp.MerchantResourceContentBlock;
import com.example.luofushan.dto.resp.MerchantResourceDetailResp;
import com.example.luofushan.security.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements com.example.luofushan.service.MerchantService {

    private final MerchantMapper merchantMapper;
    private final MerchantTokenMapper merchantTokenMapper;
    private final ResourceMapper resourceMapper;

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
}
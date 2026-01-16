package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.AdminConfig;
import com.example.luofushan.dao.entity.AdminToken;
import com.example.luofushan.dao.mapper.AdminConfigMapper;
import com.example.luofushan.dao.mapper.AdminTokenMapper;
import com.example.luofushan.dto.req.AdminPasswordUpdateReq;
import com.example.luofushan.dto.req.AdminUnlockReq;
import com.example.luofushan.dto.resp.AdminUnlockResp;
import com.example.luofushan.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminConfigMapper adminConfigMapper;
    private final AdminTokenMapper adminTokenMapper;

    @Override
    public AdminUnlockResp unlock(AdminUnlockReq req) {
        if (!StringUtils.hasText(req.getPassword())) {
            throw new LuoFuShanException("密码不能为空");
        }

        AdminConfig config = adminConfigMapper.selectOne(
                new LambdaQueryWrapper<AdminConfig>()
                        .orderByDesc(AdminConfig::getId)
                        .last("limit 1")
        );
        if (config == null) {
            throw new LuoFuShanException("管理端尚未初始化");
        }

        String inputHash = DigestUtils.md5DigestAsHex(
                req.getPassword().getBytes(StandardCharsets.UTF_8)
        );
        if (!inputHash.equals(config.getUnlockPassword())) {
            throw new LuoFuShanException("解锁密码错误");
        }

        // 生成会话 token（有效期 2 小时）
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusHours(2);

        AdminToken at = new AdminToken();
        at.setToken(token);
        at.setCreateTime(now);
        at.setExpireTime(expireTime);
        adminTokenMapper.insert(at);

        AdminUnlockResp resp = new AdminUnlockResp();
        resp.setToken(token);
        resp.setExpireTime(expireTime);
        return resp;
    }

    @Override
    public void updatePassword(AdminPasswordUpdateReq req) {
        if (!StringUtils.hasText(req.getOldPassword()) ||
                !StringUtils.hasText(req.getNewPassword())) {
            throw new LuoFuShanException("旧密码和新密码均不能为空");
        }
        if (req.getNewPassword().length() < 6) {
            throw new LuoFuShanException("新密码长度不能少于6位");
        }

        AdminConfig config = adminConfigMapper.selectOne(
                new LambdaQueryWrapper<AdminConfig>()
                        .orderByDesc(AdminConfig::getId)
                        .last("limit 1")
        );
        if (config == null) {
            throw new LuoFuShanException("管理端尚未初始化");
        }

        String oldHash = DigestUtils.md5DigestAsHex(
                req.getOldPassword().getBytes(StandardCharsets.UTF_8)
        );
        if (!oldHash.equals(config.getUnlockPassword())) {
            throw new LuoFuShanException("旧密码不正确");
        }

        String newHash = DigestUtils.md5DigestAsHex(
                req.getNewPassword().getBytes(StandardCharsets.UTF_8)
        );
        config.setUnlockPassword(newHash);
        adminConfigMapper.updateById(config);

        //修改密码后清空所有已有的管理端 token
        adminTokenMapper.delete(null);
    }
}

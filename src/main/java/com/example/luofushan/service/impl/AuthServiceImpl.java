package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.luofushan.config.WeChatProperties;
import com.example.luofushan.config.AuthProperties;
import com.example.luofushan.dao.entity.User;
import com.example.luofushan.dao.entity.UserToken;
import com.example.luofushan.dao.mapper.UserMapper;
import com.example.luofushan.dao.mapper.UserTokenMapper;
import com.example.luofushan.dto.resp.WeChatSessionResp;
import com.example.luofushan.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final WeChatProperties weChatProps;
    private final AuthProperties authProps;   
    private final RestTemplate restTemplate;
    private final UserMapper userMapper;
    private final UserTokenMapper userTokenMapper;

    @Override
    public String loginByWeChatCode(String codeId) {
        if (!StringUtils.hasText(codeId)) {
            throw new IllegalArgumentException("codeId不能为空");
        }
        // 调用微信 jscode2session
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                weChatProps.getAppid(), weChatProps.getSecret(), codeId
        );
        String body = restTemplate.getForObject(url, String.class);
        if (!StringUtils.hasText(body)) {
            throw new RuntimeException("微信接口返回空");
        }
        WeChatSessionResp wx;
        try {
            wx = new ObjectMapper().readValue(body, WeChatSessionResp.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("解析微信返回失败", e);
        }
        if (wx.getErrcode() != null && wx.getErrcode() != 0) {
            throw new RuntimeException("微信登录失败: " + wx.getErrmsg());
        }
        String openId = wx.getOpenid();
        if (!StringUtils.hasText(openId)) {
            throw new RuntimeException("未获取到openid");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openId));
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setNickname("游客");
            user.setAvatarUrl(null);
            user.setPoints(0);
            userMapper.insert(user);
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        Date expireAt = Date.from(Instant.now().plus(authProps.getTokenTtlDays(), ChronoUnit.DAYS));

        userTokenMapper.delete(new LambdaQueryWrapper<UserToken>().eq(UserToken::getUserId, user.getId()));

        UserToken ut = new UserToken();
        ut.setUserId(user.getId());
        ut.setToken(token);
        ut.setExpireAt(expireAt);
        userTokenMapper.insert(ut);

        return token;
    }
}

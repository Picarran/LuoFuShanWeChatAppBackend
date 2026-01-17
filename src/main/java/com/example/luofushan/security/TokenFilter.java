package com.example.luofushan.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.luofushan.dao.entity.UserToken;
import com.example.luofushan.dao.mapper.UserTokenMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenFilter implements Filter {

    private final UserTokenMapper userTokenMapper;

    // 简单白名单（前缀匹配）
    private static final List<String> WHITELIST_PREFIX = List.of(
            "/auth/login",
            "/resource/nearby",
            "/resource/list",
            "/resource/content",
            "/resource/nearby/content",
            "/checkin/location/list",
            "/mall/item/list", 
            "/admin",
            "/merchant",  
            "/v3/api-docs", "/swagger", "/swagger-ui", "/favicon", "/error" 
    );

    private boolean isWhitelisted(String uri) {
        if (uri == null) return true;
        for (String p : WHITELIST_PREFIX) {
            if (uri.startsWith(p)) return true;
        }
        return false;
    }

    private void write401(ServletResponse response, String message) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json;charset=UTF-8");
        String json = "{\"code\":401,\"msg\":\"" + message + "\",\"data\":null}";
        resp.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        try {
            String uri = req.getRequestURI();

            // 放行白名单
            if (isWhitelisted(uri)) {
                chain.doFilter(request, response);
                return;
            }

            String authHeader = req.getHeader("Authorization");
            String token = null;
            if (authHeader != null && !authHeader.isBlank()) {
                if (authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7).trim();
                } else {
                    token = authHeader.trim();
                }
            }

            if (token == null || token.isBlank()) {
                write401(response, "未携带登录凭证");
                return;
            }

            UserToken ut = userTokenMapper.selectOne(new LambdaQueryWrapper<UserToken>()
                    .eq(UserToken::getToken, token));

            if (ut == null || ut.getExpireAt() == null || !ut.getExpireAt().after(new Date())) {
                write401(response, "登录凭证无效或已过期");
                return;
            }

            UserContext.setUserId(ut.getUserId());

            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
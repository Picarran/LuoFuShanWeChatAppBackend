package com.example.luofushan.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.luofushan.dao.entity.MerchantToken;
import com.example.luofushan.dao.mapper.MerchantTokenMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MerchantTokenFilter implements Filter {

    private final MerchantTokenMapper merchantTokenMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        if (!uri.startsWith("/merchant")) {
            chain.doFilter(request, response);
            return;
        }
        if (uri.startsWith("/merchant/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        try {
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
                write401(response, "未携带商家登录凭证");
                return;
            }

            MerchantToken mt = merchantTokenMapper.selectOne(
                    new LambdaQueryWrapper<MerchantToken>().eq(MerchantToken::getToken, token)
            );
            if (mt == null || mt.getExpireAt() == null || mt.getExpireAt().isBefore(LocalDateTime.now())) {
                write401(response, "商家登录凭证无效或已过期");
                return;
            }

            MerchantContext.setMerchantId(mt.getMerchantId());
            chain.doFilter(request, response);
        } finally {
            MerchantContext.clear();
        }
    }

    private void write401(ServletResponse response, String message) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json;charset=UTF-8");
        String json = "{\"code\":401,\"msg\":\"" + message + "\",\"data\":null}";
        resp.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
    }
}
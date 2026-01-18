package com.example.luofushan.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.luofushan.dao.entity.AdminToken;
import com.example.luofushan.dao.mapper.AdminTokenMapper;
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
public class AdminFilter implements Filter {

    private final AdminTokenMapper adminTokenMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        if (!uri.startsWith("/admin")) {
            chain.doFilter(request, response);
            return;
        }
        if (uri.startsWith("/admin/unlock")) {
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
                write401(response, "未携带管理员登录凭证");
                return;
            }

            AdminToken at = adminTokenMapper.selectOne(
                    new LambdaQueryWrapper<AdminToken>().eq(AdminToken::getToken, token)
            );
            if (at == null || at.getExpireTime() == null || at.getExpireTime().isBefore(LocalDateTime.now())) {
                write401(response, "管理员登录凭证无效或已过期");
                return;
            }
            chain.doFilter(request, response);
        } finally {
            ;
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

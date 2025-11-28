package com.example.luofushan.controller;

import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.LoginReq;
import com.example.luofushan.dto.resp.LoginResp;
import com.example.luofushan.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public Result<LoginResp> login(@RequestBody LoginReq req) {
        String token = authService.loginByWeChatCode(req.getCodeId());
        return Result.buildSuccess(new LoginResp(token));
    }
}

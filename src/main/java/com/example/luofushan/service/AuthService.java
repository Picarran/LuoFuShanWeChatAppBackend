package com.example.luofushan.service;

import com.example.luofushan.dto.resp.LoginResp;

public interface AuthService {
    LoginResp login(String codeId, String appId, String secret);
}
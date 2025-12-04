package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class LoginReq {
    private String codeId;
    private String appId;   
    private String secret;  
}
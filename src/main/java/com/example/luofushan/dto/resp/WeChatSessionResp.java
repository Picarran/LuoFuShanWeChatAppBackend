package com.example.luofushan.dto.resp;

import lombok.Data;

@Data
public class WeChatSessionResp {
    private String openid;
    private String session_key;
    private String unionid;
    private Integer errcode;
    private String errmsg;
}
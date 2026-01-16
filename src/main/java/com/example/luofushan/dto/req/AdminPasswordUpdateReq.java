package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class AdminPasswordUpdateReq {
    private String oldPassword;
    private String newPassword;
}

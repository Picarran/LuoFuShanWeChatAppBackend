package com.example.luofushan.dto.resp;

import lombok.Data;

@Data
public class MerchantResourceContentBlock {
    private String type;  // text / image / video
    private String value; // 文本 / URL
}
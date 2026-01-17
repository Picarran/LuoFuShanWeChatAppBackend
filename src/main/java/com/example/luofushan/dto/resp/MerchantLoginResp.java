package com.example.luofushan.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantLoginResp {
    private Long merchantId;
    private String name;
    private String type;
    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;
}
package com.example.luofushan.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantRedeemResp {
    private Long exchangeId;
    private String itemName;
    private String placeName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime redeemedAt;
}

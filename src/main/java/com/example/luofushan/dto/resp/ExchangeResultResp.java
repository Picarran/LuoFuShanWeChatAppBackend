package com.example.luofushan.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExchangeResultResp {
    
    private Long recordId;
    
    private Long itemId;
    
    private String itemName;
    
    private String code;
    
    private Boolean isRedeemed;
    
    private String placeName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

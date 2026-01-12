package com.example.luofushan.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyExchangeRecordResp {
    
    private Long id;
    
    private String itemName;
    
    private Boolean isRedeemed;
    
    private String placeName;
    
    private String code;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

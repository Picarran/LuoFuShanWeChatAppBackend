package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class MyExchangeListReq {
    
    private String status; // redeemed / unredeemed
    
    private Integer current;
    
    private Integer size;

    public void initDefault() {
        if (current == null || current <= 0) current = 1;
        if (size == null || size <= 0) size = 10;
    }
}

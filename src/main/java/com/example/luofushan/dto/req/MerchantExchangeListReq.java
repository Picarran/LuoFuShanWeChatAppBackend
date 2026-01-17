package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class MerchantExchangeListReq {
    private String status; // redeemed / unredeemed
    private Integer page;
    private Integer size;

    public void initDefault() {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
    }
}

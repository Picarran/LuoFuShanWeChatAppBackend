package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class MerchantMallItemListReq {
    private Integer page;
    private Integer size;
    private String sort; // points_desc / points_asc

    public void initDefault() {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        if (sort == null || sort.isEmpty()) sort = "points_asc";
    }
}
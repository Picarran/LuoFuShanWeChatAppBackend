package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class MallItemListReq {
    
    private String sort; // points_desc / points_asc
    
    private Integer current;
    
    private Integer size;

    public void initDefault() {
        if (current == null || current <= 0) current = 1;
        if (size == null || size <= 0) size = 10;
        if (sort == null || sort.isEmpty()) sort = "points_asc";
    }
}

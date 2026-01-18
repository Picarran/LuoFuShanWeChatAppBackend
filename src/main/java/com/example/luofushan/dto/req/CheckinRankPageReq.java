package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class CheckinRankPageReq {
    private Integer page;
    private Integer size;
    private String type;

    public void initDefault() {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
    }
}
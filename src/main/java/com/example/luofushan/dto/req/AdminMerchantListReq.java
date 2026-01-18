package com.example.luofushan.dto.req;

import io.netty.util.internal.StringUtil;
import lombok.Data;

@Data
public class AdminMerchantListReq {
    private Integer page;
    private Integer size;
    private String type;
    private String fuzzy;
    private Integer status;

    public void initDefault() {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        if (StringUtil.isNullOrEmpty(type)) type = "ALL";
        if (StringUtil.isNullOrEmpty(fuzzy)) fuzzy = "";
        if(status == null || (status!= 0 && status != 1)) status = -1;
    }
}

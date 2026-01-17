package com.example.luofushan.dto.req;

import com.example.luofushan.dto.resp.MerchantResourceContentBlock;
import lombok.Data;

import java.util.List;

@Data
public class MerchantResourceContentUpdateReq {
    private List<MerchantResourceContentBlock> content;
}
package com.example.luofushan.service;

import com.example.luofushan.dto.req.MerchantLoginReq;
import com.example.luofushan.dto.req.MerchantResourceBaseUpdateReq;
import com.example.luofushan.dto.req.MerchantResourceContentUpdateReq;
import com.example.luofushan.dto.resp.MerchantLoginResp;
import com.example.luofushan.dto.resp.MerchantProfileResp;
import com.example.luofushan.dto.resp.MerchantResourceDetailResp;

public interface MerchantService {

    MerchantLoginResp login(MerchantLoginReq req);

    MerchantProfileResp getProfile();

    MerchantResourceDetailResp getResourceDetail();

    void updateResourceBase(MerchantResourceBaseUpdateReq req);

    void updateResourceContent(MerchantResourceContentUpdateReq req);
}
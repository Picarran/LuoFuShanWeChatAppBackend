package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;

public interface MerchantService {

    MerchantLoginResp login(MerchantLoginReq req);

    MerchantProfileResp getProfile();

    MerchantResourceDetailResp getResourceDetail();

    void updateResourceBase(MerchantResourceBaseUpdateReq req);

    void updateResourceContent(MerchantResourceContentUpdateReq req);

    Page<MallItemListResp> getMallItemList(MerchantMallItemListReq req);

    Page<MerchantExchangeRecordResp> getExchangeList(MerchantExchangeListReq req);

    MerchantRedeemResp redeemCode(MerchantRedeemReq req);

    MerchantUserPointsResp getUserPoints(MerchantUserPointsReq req);

    MerchantUserPointsResp updateUserPoints(MerchantPointsUpdateReq req);
}
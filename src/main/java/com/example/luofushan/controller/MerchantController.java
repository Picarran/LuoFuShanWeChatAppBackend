package com.example.luofushan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.service.MerchantService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    @PostMapping("/auth/login")
    public Result<MerchantLoginResp> login(@RequestBody MerchantLoginReq req) {
        return Result.buildSuccess(merchantService.login(req));
    }

    @GetMapping("/profile")
    public Result<MerchantProfileResp> profile() {
        return Result.buildSuccess(merchantService.getProfile());
    }

    @GetMapping("/resource/detail")
    public Result<MerchantResourceDetailResp> resourceDetail() {
        return Result.buildSuccess(merchantService.getResourceDetail());
    }

    @PutMapping("/resource/base")
    public Result<Void> updateResourceBase(@RequestBody MerchantResourceBaseUpdateReq req) {
        merchantService.updateResourceBase(req);
        return Result.buildSuccess(null);
    }

    @PutMapping("/resource/content")
    public Result<Void> updateResourceContent(@RequestBody MerchantResourceContentUpdateReq req) {
        merchantService.updateResourceContent(req);
        return Result.buildSuccess(null);
    }

    @GetMapping("/mall/item/list")
    public Result<Page<MallItemListResp>> getMallItemList(MerchantMallItemListReq req) {
        return Result.buildSuccess(merchantService.getMallItemList(req));
    }

    @GetMapping("/exchange/list")
    public Result<Page<MerchantExchangeRecordResp>> getExchangeList(MerchantExchangeListReq req) {
        return Result.buildSuccess(merchantService.getExchangeList(req));
    }

    @PostMapping("/exchange/redeem")
    public Result<MerchantRedeemResp> redeemCode(@RequestBody MerchantRedeemReq req) {
        return Result.buildSuccess(merchantService.redeemCode(req));
    }

    @GetMapping("/user/points")
    public Result<MerchantUserPointsResp> getUserPoints(MerchantUserPointsReq req) {
        return Result.buildSuccess(merchantService.getUserPoints(req));
    }

    @PostMapping("/user/points/update")
    public Result<MerchantUserPointsResp> updateUserPoints(@RequestBody MerchantPointsUpdateReq req) {
        return Result.buildSuccess(merchantService.updateUserPoints(req));
    }
}
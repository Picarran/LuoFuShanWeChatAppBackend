package com.example.luofushan.controller;

import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.MerchantLoginReq;
import com.example.luofushan.dto.req.MerchantResourceBaseUpdateReq;
import com.example.luofushan.dto.req.MerchantResourceContentUpdateReq;
import com.example.luofushan.dto.resp.MerchantLoginResp;
import com.example.luofushan.dto.resp.MerchantProfileResp;
import com.example.luofushan.dto.resp.MerchantResourceDetailResp;
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
}
package com.example.luofushan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.service.AdminService;
import com.example.luofushan.service.CheckinService;
import com.example.luofushan.service.ResourceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;
    @Resource
    private ResourceService resourceService;
    @Resource
    private CheckinService checkinService;

    @PostMapping("/unlock")
    public Result<AdminUnlockResp> unlock(@RequestBody AdminUnlockReq req) {
        return Result.buildSuccess(adminService.unlock(req));
    }

    @PostMapping("/password/update")
    public Result<Void> updatePassword(@RequestBody AdminPasswordUpdateReq req) {
        adminService.updatePassword(req);
        return Result.buildSuccess(null);
    }

    @PostMapping("/resource/save")
    public Result<AdminSaveResourceResp> saveResource(@RequestBody AdminSaveResourceReq req) {
        return Result.buildSuccess(adminService.saveResource(req));
    }

    @PostMapping("/resource/delete")
    public Result<String> deleteResource(@RequestParam Long id) {
        return Result.buildSuccess(adminService.deleteResource(id));
    }

    @GetMapping("/resource/list")
    public Result<Page<ResourcePageResp>> list(ResourcePageReq req) {
        Page<ResourcePageResp> result = resourceService.queryPage(req);
        return Result.buildSuccess(result);
    }

    @PostMapping("/checkin/location/save")
    public Result<AdminSaveCheckinLocationResp> saveCheckinLocation(@RequestBody AdminSaveCheckinLocationReq req) {
        return Result.buildSuccess(adminService.saveCheckinLocation(req));
    }

    @PostMapping("/checkin/location/delete")
    public Result<String> deleteCheckinLocation(@RequestParam Long id) {
        return Result.buildSuccess(adminService.deleteCheckinLocation(id));
    }

    @GetMapping("/checkin/location/list")
    public Result<List<CheckinLocationListResp>> getAllLocations() {
        List<CheckinLocationListResp> locations = checkinService.getAllLocations();
        return Result.buildSuccess(locations);
    }

    @PostMapping("/merchant")
    public Result<AdminCreateMerchantResp> createMerchant(@RequestBody AdminCreateMerchantReq req) {
        return Result.buildSuccess(adminService.createMerchant(req));
    }

    @GetMapping("/merchant/list")
    public Result<Page<AdminMerchantListResp>> getMerchatlist(AdminMerchantListReq req) {
        return Result.buildSuccess(adminService.listMerchant(req));
    }

    @GetMapping("/merchant/detail")
    public Result<AdminMerchantDetailResp> getMerchantDetail(@RequestParam Long id) {
        return Result.buildSuccess(adminService.getMerchantDetail(id));
    }

    @PostMapping("/merchant/update")
    public Result<AdminUpdateMerchantResp> updateMerchant(@RequestBody AdminUpdateMerchantReq req) {
        return Result.buildSuccess(adminService.updateMerchant(req));
    }

    @PostMapping("/merchant/password/reset")
    public Result<String> updateMerchantPassword(@RequestBody AdminUpdateMerchantPasswordReq req) {
        return Result.buildSuccess(adminService.updateMerchantPassword(req));
    }

    @PostMapping("/merchant/status/update")
    public Result<String> updateMerchantStatus(@RequestBody AdminUpdateMerchantStatusReq req) {
        return Result.buildSuccess(adminService.updateMerchantStatus(req));
    }

    @PostMapping("/merchant/delete")
    public Result<String> deleteMerchant(@RequestParam Long id) {
        return Result.buildSuccess(adminService.deleteMerchant(id));
    }
}

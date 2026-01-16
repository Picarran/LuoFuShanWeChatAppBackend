package com.example.luofushan.controller;

import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.AdminPasswordUpdateReq;
import com.example.luofushan.dto.req.AdminUnlockReq;
import com.example.luofushan.dto.resp.AdminUnlockResp;
import com.example.luofushan.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @PostMapping("/unlock")
    public Result<AdminUnlockResp> unlock(@RequestBody AdminUnlockReq req) {
        return Result.buildSuccess(adminService.unlock(req));
    }

    @PostMapping("/password/update")
    public Result<Void> updatePassword(@RequestBody AdminPasswordUpdateReq req) {
        adminService.updatePassword(req);
        return Result.buildSuccess(null);
    }
}

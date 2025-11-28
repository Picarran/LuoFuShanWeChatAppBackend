package com.example.luofushan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.NearbyResourceReq;
import com.example.luofushan.dto.req.ResourcePageReq;
import com.example.luofushan.dto.resp.NearbyResourceResp;
import com.example.luofushan.dto.resp.ResourcePageResp;
import com.example.luofushan.service.ResourceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Resource
    private ResourceService resourceService;

    @GetMapping("/content")
    public Result<String> getContentById(@RequestParam("id") Long id) {
        return Result.buildSuccess(resourceService.getContent(id));
    }

    @GetMapping("/list")
    public Result<Page<ResourcePageResp>> list(ResourcePageReq req) {
        Page<ResourcePageResp> result = resourceService.queryPage(req);
        return Result.buildSuccess(result);
    }

    @GetMapping("/nearby")
    public Result<Page<NearbyResourceResp>> nearbyResources(NearbyResourceReq req) {
        return Result.buildSuccess(resourceService.listNearbyResources(req));
    }
}

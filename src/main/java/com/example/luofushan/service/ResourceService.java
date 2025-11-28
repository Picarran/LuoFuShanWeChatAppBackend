package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.luofushan.dao.entity.Resource;
import com.example.luofushan.dto.req.NearbyResourceReq;
import com.example.luofushan.dto.req.ResourcePageReq;
import com.example.luofushan.dto.resp.NearbyResourceResp;
import com.example.luofushan.dto.resp.ResourcePageResp;

public interface ResourceService extends IService<Resource> {
    String getContent(Long id);
    Page<ResourcePageResp> queryPage(ResourcePageReq req);

    Page<NearbyResourceResp> listNearbyResources(NearbyResourceReq req);

}

package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.luofushan.dao.entity.Resource;
import com.example.luofushan.dao.mapper.ResourceMapper;
import com.example.luofushan.dto.req.ResourcePageReq;
import com.example.luofushan.dto.resp.ResourcePageResp;
import com.example.luofushan.service.ResourceService;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @Override
    public String getContent(Long id) {

        return baseMapper.selectById(id).getContentJson();
    }

    @Override
    public Page<ResourcePageResp> queryPage(ResourcePageReq req) {
        req.initDefault();
        Page<Resource> page = new Page<>(req.getPage(), req.getSize());

        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();

        // type
        if (!"ALL".equalsIgnoreCase(req.getType())) {
            wrapper.eq(Resource::getType, req.getType());
        }

        // fuzzy search
        if (!StringUtil.isNullOrEmpty(req.getFuzzy())) {
            wrapper.and(w -> w.like(Resource::getName, req.getFuzzy()));
        }

        // 排序？？ 按创建时间
        wrapper.orderByDesc(Resource::getCreatedAt);

        Page<Resource> resourcePage = this.page(page, wrapper);

        // 封装 VO
        Page<ResourcePageResp> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<ResourcePageResp> voList = resourcePage.getRecords().stream().map(r -> ResourcePageResp.builder()
                .id(r.getId())
                .type(r.getType())
                .name(r.getName())
                .coverImg(r.getCoverImg())
                .hotScore(r.getHotScore())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .build()).toList();

        voPage.setRecords(voList);

        return voPage;
    }

}

package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.Resource;
import com.example.luofushan.dao.mapper.ResourceMapper;
import com.example.luofushan.dto.req.NearbyResourceReq;
import com.example.luofushan.dto.req.ResourcePageReq;
import com.example.luofushan.dto.resp.NearbyResourceResp;
import com.example.luofushan.dto.resp.ResourcePageResp;
import com.example.luofushan.service.ResourceService;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @Override
    public String getContent(Long id) {
         Resource resource = baseMapper.selectById(id);
         if(resource==null) {
             throw LuoFuShanException.resourceNotExists();
         }
         return resource.getContentJson();
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
        List<ResourcePageResp> voList = resourcePage.getRecords().stream().map(r -> ResourcePageResp.builder()
                .id(r.getId())
                .type(r.getType())
                .name(r.getName())
                .coverImg(r.getCoverImg())
                .hotScore(r.getHotScore())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .build()).toList();

        // 直接复用 resourcePage 的分页信息
        Page<ResourcePageResp> voPage = new Page<>();
        voPage.setCurrent(resourcePage.getCurrent());
        voPage.setSize(resourcePage.getSize());
        voPage.setTotal(resourcePage.getTotal());
        voPage.setPages(resourcePage.getPages());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public Page<NearbyResourceResp> listNearbyResources(NearbyResourceReq req) {
        req.initDefault();
        int offset = (req.getPage() - 1) * req.getSize();

        List<NearbyResourceResp> records = baseMapper.selectNearbyResources(req.getType(), req.getLatitude(), req.getLongitude(), offset, req.getSize(), req.getSortBy());
        int total = baseMapper.countNearbyResources(req.getType());

        Page<NearbyResourceResp> result = new Page<>(req.getPage(), req.getSize(), total);
        result.setRecords(records);
        result.setPages((total + req.getSize() - 1) / req.getSize());

        return result;
    }

}

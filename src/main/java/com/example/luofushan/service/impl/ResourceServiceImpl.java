package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.Resource;
import com.example.luofushan.dao.mapper.ResourceMapper;
import com.example.luofushan.dto.req.NearbyResourceContentReq;
import com.example.luofushan.dto.req.NearbyResourceReq;
import com.example.luofushan.dto.req.ResourcePageReq;
import com.example.luofushan.dto.resp.NearbyResourceContentResp;
import com.example.luofushan.dto.resp.NearbyResourceResp;
import com.example.luofushan.dto.resp.ResourcePageResp;
import com.example.luofushan.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @Override
    public String getContent(Long id) {
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resource::getDelflag, 0)
                .eq(Resource::getId, id);
         Resource resource = baseMapper.selectOne(wrapper);
         if(resource==null) {
             throw LuoFuShanException.resourceNotExists();
         }
         return resource.getContentJson();
    }

    @Override
    public Page<ResourcePageResp> queryPage(ResourcePageReq req) {
        req.initDefault();
        int offset = (req.getPage() - 1) * req.getSize();

        // 排序？？ 按热度
        String sortBy = "hot";
        List<ResourcePageResp> records = baseMapper.selectResourcePage(req.getType(), req.getFuzzy(), sortBy, offset, req.getSize());

        int total = baseMapper.countResource(req.getType(), req.getFuzzy());

        Page<ResourcePageResp> page = new Page<>(req.getPage(),req.getSize(),total);
        page.setPages((total + req.getSize() - 1) / req.getSize());
        page.setRecords(records);

        return page;
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

    @Override
    public NearbyResourceContentResp getResourceContent(NearbyResourceContentReq req) {
        NearbyResourceContentResp resp = baseMapper.selectResourceContent(req.getId(), req.getLatitude(), req.getLongitude());
        return resp;
    }
}

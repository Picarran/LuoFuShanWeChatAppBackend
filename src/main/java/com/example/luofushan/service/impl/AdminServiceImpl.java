package com.example.luofushan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.*;
import com.example.luofushan.dao.mapper.*;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.service.AdminService;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminConfigMapper adminConfigMapper;
    private final AdminTokenMapper adminTokenMapper;
    private final ResourceMapper resourceMapper;
    private final CheckinLocationMapper checkinLocationMapper;
    private final MerchantMapper merchantMapper;

    @Override
    public AdminUnlockResp unlock(AdminUnlockReq req) {
        if (!StringUtils.hasText(req.getPassword())) {
            throw new LuoFuShanException("密码不能为空");
        }

        AdminConfig config = adminConfigMapper.selectOne(
                new LambdaQueryWrapper<AdminConfig>()
                        .orderByDesc(AdminConfig::getId)
                        .last("limit 1")
        );
        if (config == null) {
            throw new LuoFuShanException("管理端尚未初始化");
        }

        String inputHash = DigestUtils.md5DigestAsHex(
                req.getPassword().getBytes(StandardCharsets.UTF_8)
        );
        if (!inputHash.equals(config.getUnlockPassword())) {
            throw new LuoFuShanException("解锁密码错误");
        }

        // 生成会话 token（有效期 2 小时）
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusHours(2);

        AdminToken at = new AdminToken();
        at.setToken(token);
        at.setCreateTime(now);
        at.setExpireTime(expireTime);
        adminTokenMapper.insert(at);

        AdminUnlockResp resp = new AdminUnlockResp();
        resp.setToken(token);
        resp.setExpireTime(expireTime);
        return resp;
    }

    @Override
    public void updatePassword(AdminPasswordUpdateReq req) {
        if (!StringUtils.hasText(req.getOldPassword()) ||
                !StringUtils.hasText(req.getNewPassword())) {
            throw new LuoFuShanException("旧密码和新密码均不能为空");
        }
        if (req.getNewPassword().length() < 6) {
            throw new LuoFuShanException("新密码长度不能少于6位");
        }

        AdminConfig config = adminConfigMapper.selectOne(
                new LambdaQueryWrapper<AdminConfig>()
                        .orderByDesc(AdminConfig::getId)
                        .last("limit 1")
        );
        if (config == null) {
            throw new LuoFuShanException("管理端尚未初始化");
        }

        String oldHash = DigestUtils.md5DigestAsHex(
                req.getOldPassword().getBytes(StandardCharsets.UTF_8)
        );
        if (!oldHash.equals(config.getUnlockPassword())) {
            throw new LuoFuShanException("旧密码不正确");
        }

        String newHash = DigestUtils.md5DigestAsHex(
                req.getNewPassword().getBytes(StandardCharsets.UTF_8)
        );
        config.setUnlockPassword(newHash);
        adminConfigMapper.updateById(config);

        //修改密码后清空所有已有的管理端 token
        adminTokenMapper.delete(null);
    }

    @Override
    public AdminSaveResourceResp saveResource(AdminSaveResourceReq req) {
        List<String> types = List.of("景点", "住宿", "餐饮", "商家");
        if(StringUtil.isNullOrEmpty(req.getName()) || StringUtil.isNullOrEmpty(req.getType())) {
            throw LuoFuShanException.adminFail("名称或类型为空");
        }
        if(!types.contains(req.getType())) {
            throw LuoFuShanException.adminFail("资源类型不为：景点/住宿/餐饮/商家");
        }
        Resource resource;
        // 插入
        if(req.getId() == null) {
            resource = BeanUtil.toBean(req, Resource.class);
            resourceMapper.insert(resource);

        }
        // 更新
        else {
            LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Resource::getDelflag, 0)
                    .eq(Resource::getId, req.getId());
            resource = resourceMapper.selectOne(wrapper);
            if(resource==null) throw LuoFuShanException.resourceNotExists();
            if(req.getLatitude()!=null) resource.setLatitude(req.getLatitude());
            if(req.getLongitude()!=null) resource.setLongitude(req.getLongitude());
            if(req.getContentJson()!=null) resource.setContentJson(req.getContentJson());
            if(req.getName()!=null) resource.setName(req.getName());
            if(req.getCoverImg()!=null) resource.setCoverImg(req.getCoverImg());
            if(req.getHotScore()!=null) resource.setHotScore(req.getHotScore());
            if(req.getType()!=null) resource.setType(req.getType());
            resourceMapper.updateById(resource);
        }
        return BeanUtil.toBean(resource, AdminSaveResourceResp.class);
    }

    @Override
    public String deleteResource(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if(resource==null) {
            throw LuoFuShanException.resourceNotExists();
        }
        resource.setDelflag(1);
        resourceMapper.updateById(resource);
        return "删除成功";
    }

    @Override
    public AdminSaveCheckinLocationResp saveCheckinLocation(AdminSaveCheckinLocationReq req) {
        CheckinLocation checkinLocation;
        // 插入
        if(req.getId() == null) {
            checkinLocation = BeanUtil.toBean(req, CheckinLocation.class);
            checkinLocation.setTodayHasCheckin(0L);
            checkinLocationMapper.insert(checkinLocation);
        }
        // 更新
        else {
            LambdaQueryWrapper<CheckinLocation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CheckinLocation::getDelflag, 0)
                    .eq(CheckinLocation::getId, req.getId());
            checkinLocation = checkinLocationMapper.selectOne(wrapper);
            if(checkinLocation==null) throw LuoFuShanException.checkinLocationNotExists();
            if(req.getLatitude()!=null) checkinLocation.setLatitude(req.getLatitude());
            if(req.getLongitude()!=null) checkinLocation.setLongitude(req.getLongitude());
            if(req.getName()!=null) checkinLocation.setName(req.getName());
            if(req.getCoverImg()!=null) checkinLocation.setCoverImg(req.getCoverImg());
            if(req.getScore()!=null) checkinLocation.setScore(req.getScore());
            checkinLocationMapper.updateById(checkinLocation);
        }
        return BeanUtil.toBean(checkinLocation, AdminSaveCheckinLocationResp.class);
    }

    @Override
    public String deleteCheckinLocation(Long id) {
        CheckinLocation checkinLocation = checkinLocationMapper.selectById(id);
        if(checkinLocation==null) {
            throw LuoFuShanException.checkinLocationNotExists();
        }
        checkinLocation.setDelflag(1);
        checkinLocationMapper.updateById(checkinLocation);
        return "删除成功";
    }

    @Override
    public AdminCreateMerchantResp createMerchant(AdminCreateMerchantReq req) {
        if(StringUtil.isNullOrEmpty(req.getName()) || StringUtil.isNullOrEmpty(req.getType())
        || StringUtil.isNullOrEmpty(req.getUsername()) || StringUtil.isNullOrEmpty(req.getPassword())) {
            throw LuoFuShanException.adminFail("名称或类型为空");
        }
        List<String> types = List.of("景点", "住宿", "餐饮", "商家");
        if(!types.contains(req.getType())) {
            throw LuoFuShanException.adminFail("类型不为：景点/住宿/餐饮/商家");
        }
        if(req.getResourceId()==null) {
            throw LuoFuShanException.adminFail("资源id为空");
        }

        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resource::getDelflag, 0)
                .eq(Resource::getId, req.getResourceId());
        Resource resource = resourceMapper.selectOne(wrapper);
        if(resource==null) {
            throw LuoFuShanException.adminFail("无资源id对应的资源");
        }

        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes(StandardCharsets.UTF_8)));
        Merchant merchant = BeanUtil.toBean(req, Merchant.class);
        merchant.setStatus(1);
        merchantMapper.insert(merchant);
        return BeanUtil.toBean(merchant, AdminCreateMerchantResp.class);
    }

    @Override
    public Page<AdminMerchantListResp> listMerchant(AdminMerchantListReq req) {
        req.initDefault();
        int offset = (req.getPage() - 1) * req.getSize();
        List<AdminMerchantListResp> records = merchantMapper.selectMerchantPage(req.getType(), req.getFuzzy(), offset, req.getSize(), req.getStatus());
        int total = merchantMapper.countMerchant(req.getType(), req.getFuzzy(), req.getStatus());

        Page<AdminMerchantListResp> page = new Page<>(req.getPage(),req.getSize(),total);
        page.setPages((total + req.getSize() - 1) / req.getSize());
        page.setRecords(records);

        return page;
    }

    @Override
    public AdminMerchantDetailResp getMerchantDetail(Long id) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getDelflag, 0)
                .eq(Merchant::getId, id);
        Merchant merchant = merchantMapper.selectOne(wrapper);
        if(merchant==null) throw LuoFuShanException.adminFail("商家不存在");

        if(merchant.getResourceId()==null) throw LuoFuShanException.adminFail("商家所持有的资源不存在");
        LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLambdaQueryWrapper.eq(Resource::getDelflag, 0)
                .eq(Resource::getId, merchant.getResourceId());
        Resource resource = resourceMapper.selectOne(resourceLambdaQueryWrapper);
        if(resource==null) throw LuoFuShanException.adminFail("商家所持有的资源不存在");

        AdminMerchantDetailResp resp = BeanUtil.toBean(merchant, AdminMerchantDetailResp.class);
        resp.setResourceName(resource.getName());

        return resp;
    }

    @Override
    public AdminUpdateMerchantResp updateMerchant(AdminUpdateMerchantReq req) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getDelflag, 0)
                .eq(Merchant::getId, req.getId());
        Merchant merchant = merchantMapper.selectOne(wrapper);
        if(merchant==null) throw LuoFuShanException.adminFail("商家不存在");

//        if(req.getContactName()!=null) merchant.setContcatName()
//        if(req.getContactPhone()!=null) merchant.setContactPhone()
        if(req.getStatus()!=null) {
            if(req.getStatus()!=0 && req.getStatus()!=1) {
                throw LuoFuShanException.adminFail("启用状态只能为0或1");
            }
            merchant.setStatus(req.getStatus());
        }
        if(req.getName()!=null) merchant.setName(req.getName());
        merchantMapper.updateById(merchant);

        return BeanUtil.toBean(merchant, AdminUpdateMerchantResp.class);
    }

    @Override
    public String updateMerchantPassword(AdminUpdateMerchantPasswordReq req) {
        if(req.getId()==null || StringUtil.isNullOrEmpty(req.getNewPassword())) {
            throw LuoFuShanException.adminFail("商家id为空或新密码为空");
        }

        LambdaQueryWrapper<Merchant> merchantLambdaQueryWrapper = new LambdaQueryWrapper<>();
        merchantLambdaQueryWrapper.eq(Merchant::getDelflag, 0)
                .eq(Merchant::getId, req.getId());
        Merchant merchant = merchantMapper.selectOne(merchantLambdaQueryWrapper);
        if(merchant==null) throw LuoFuShanException.adminFail("商家不存在");
        merchant.setPassword(DigestUtils.md5DigestAsHex(req.getNewPassword().getBytes(StandardCharsets.UTF_8)));
        merchantMapper.updateById(merchant);
        return "修改成功";
    }

    @Override
    public String updateMerchantStatus(AdminUpdateMerchantStatusReq req) {
        if(req.getId()==null || req.getStatus()==null) {
            throw LuoFuShanException.adminFail("商家id为空或新状态为空");
        }
        if(req.getStatus()!=1 && req.getStatus()!=0) {
            throw LuoFuShanException.adminFail("状态必须是0或1");
        }

        LambdaQueryWrapper<Merchant> merchantLambdaQueryWrapper = new LambdaQueryWrapper<>();
        merchantLambdaQueryWrapper.eq(Merchant::getDelflag, 0)
                .eq(Merchant::getId, req.getId());
        Merchant merchant = merchantMapper.selectOne(merchantLambdaQueryWrapper);
        if(merchant==null) throw LuoFuShanException.adminFail("商家不存在");
        merchant.setStatus(req.getStatus());
        merchantMapper.updateById(merchant);
        return "修改成功";
    }

    @Override
    public String deleteMerchant(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if(merchant==null) {
            throw LuoFuShanException.adminFail("商家不存在");
        }
        merchant.setDelflag(1);
        merchantMapper.updateById(merchant);
        return "删除成功";
    }
}

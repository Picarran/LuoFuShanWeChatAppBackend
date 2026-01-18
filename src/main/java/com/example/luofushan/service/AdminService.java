package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.*;

public interface AdminService {

    AdminUnlockResp unlock(AdminUnlockReq req);

    void updatePassword(AdminPasswordUpdateReq req);

    AdminSaveResourceResp saveResource(AdminSaveResourceReq req);

    String deleteResource(Long id);

    AdminSaveCheckinLocationResp saveCheckinLocation(AdminSaveCheckinLocationReq req);

    String deleteCheckinLocation(Long id);

    AdminCreateMerchantResp createMerchant(AdminCreateMerchantReq req);

    Page<AdminMerchantListResp> listMerchant(AdminMerchantListReq req);

    AdminMerchantDetailResp getMerchantDetail(Long id);

    AdminUpdateMerchantResp updateMerchant(AdminUpdateMerchantReq req);

    String updateMerchantPassword(AdminUpdateMerchantPasswordReq req);

    String updateMerchantStatus(AdminUpdateMerchantStatusReq req);

    String deleteMerchant(Long id);
}

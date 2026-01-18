package com.example.luofushan.service;

import com.example.luofushan.dto.req.*;
import com.example.luofushan.dto.resp.AdminCreateMerchantResp;
import com.example.luofushan.dto.resp.AdminSaveCheckinLocationResp;
import com.example.luofushan.dto.resp.AdminSaveResourceResp;
import com.example.luofushan.dto.resp.AdminUnlockResp;

public interface AdminService {

    AdminUnlockResp unlock(AdminUnlockReq req);

    void updatePassword(AdminPasswordUpdateReq req);

    AdminSaveResourceResp saveResource(AdminSaveResourceReq req);

    String deleteResource(Long id);

    AdminSaveCheckinLocationResp saveCheckinLocation(AdminSaveCheckinLocationReq req);

    String deleteCheckinLocation(Long id);

    AdminCreateMerchantResp createMerchant(AdminCreateMerchantReq req);

}

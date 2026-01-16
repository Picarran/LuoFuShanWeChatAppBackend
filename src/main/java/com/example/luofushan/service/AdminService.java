package com.example.luofushan.service;

import com.example.luofushan.dto.req.AdminPasswordUpdateReq;
import com.example.luofushan.dto.req.AdminUnlockReq;
import com.example.luofushan.dto.resp.AdminUnlockResp;

public interface AdminService {

    AdminUnlockResp unlock(AdminUnlockReq req);

    void updatePassword(AdminPasswordUpdateReq req);
}

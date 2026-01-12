package com.example.luofushan.service;

import com.example.luofushan.dto.req.UpdateUserProfileReq;
import com.example.luofushan.dto.resp.UserProfileResp;

public interface UserCenterService {

    UserProfileResp getProfile();

    UserProfileResp updateProfile(UpdateUserProfileReq req);
}

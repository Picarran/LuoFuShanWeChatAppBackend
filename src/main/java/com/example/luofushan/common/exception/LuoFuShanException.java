package com.example.luofushan.common.exception;

public class LuoFuShanException extends RuntimeException {

    public LuoFuShanException(String message) {
        super(message);
    }

    public static LuoFuShanException userAlreadyExists() {
        return new LuoFuShanException("用户名已经存在!");
    }

    public static LuoFuShanException resourceNotExists() {
        return new LuoFuShanException("景点/商家/住宿/餐饮不存在!");
    }

    public static LuoFuShanException checkinLocationNotExists() {
        return new LuoFuShanException("打卡点不存在!");
    }

    public static LuoFuShanException alreadyHit() {
        return new LuoFuShanException("用户已在该打卡点打过卡");
    }

    public static LuoFuShanException hasNoDistanceInfo() {
        return new LuoFuShanException("没有经纬度信息");
    }

    public static LuoFuShanException UserOrPostNotExists() {
        return new LuoFuShanException("用户或动态不存在");
    }

    public static LuoFuShanException invalidStatus() {
        return new LuoFuShanException("状态参数无效，必须为 redeemed 或 unredeemed");
    }

    public static LuoFuShanException mallItemNotExists() {
        return new LuoFuShanException("兑换商品不存在");
    }

    public static LuoFuShanException pointsNotEnough() {
        return new LuoFuShanException("积分不足，无法兑换");
    }

    public static LuoFuShanException fileUploadFailed(String msg) {
        return new LuoFuShanException(msg);
    }

    public static LuoFuShanException adminFail(String msg) {return new LuoFuShanException(msg);}
}
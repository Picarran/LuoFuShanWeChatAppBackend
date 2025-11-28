package com.example.luofushan.common.exception;

public class LuoFuShanException extends RuntimeException {

    public LuoFuShanException(String message) {
        super(message);
    }

    public static LuoFuShanException userAlreadyExists() {
        return new LuoFuShanException("用户名已经存在!");
    }

    public static LuoFuShanException userNotExists() {
        return new LuoFuShanException("用户名不存在!");
    }

    public static LuoFuShanException notLogin() {
        return new LuoFuShanException("未登录!");
    }

    public static LuoFuShanException userNameOrPasswordError() {
        return new LuoFuShanException("用户名或密码错误!");
    }

    public static LuoFuShanException productNotExists() {
        return new LuoFuShanException("商品不存在!");
    }

    public static LuoFuShanException adNotExist() {
        return new LuoFuShanException("广告不存在!");
    }

    public static LuoFuShanException ImageUploadFail() {
        return new LuoFuShanException("图片上传失败");
    }

    public static LuoFuShanException ImageDeleteFail() {
        return new LuoFuShanException("图片删除失败");
    }

    public static LuoFuShanException articleNotExists() {
        return new LuoFuShanException("文章不存在!");
    }

    public static LuoFuShanException commentNotExists() {
        return new LuoFuShanException("评论不存在!");
    }

    public static LuoFuShanException replyNotExists() {
        return new LuoFuShanException("回复不存在!");
    }

    public static LuoFuShanException orderNotExists() {
        return new LuoFuShanException("订单不存在!");
    }

    public static LuoFuShanException productNotEnough() {
        return new LuoFuShanException("商品数量不足");
    }

    public static LuoFuShanException cartItemNotExists() {
        return new LuoFuShanException("购物车商品不存在");
    }

    public static LuoFuShanException cartItemExists() {
        return new LuoFuShanException("购物车已经存在该商品");
    }

    public static LuoFuShanException alipayApiException() {
        return new LuoFuShanException("alipay异常");
    }

    public static LuoFuShanException followAlreadyExist() {return new LuoFuShanException("关注用户已经关注了");}

    public static LuoFuShanException favoriteAlreadyExist() {return new LuoFuShanException("已经收藏过啦！");}

    public static LuoFuShanException AlreadyLiked() {
        return new LuoFuShanException("不能重复点赞！");
    }
}
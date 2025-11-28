package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("open_id")
    private String openId;

    private String nickname;

    @TableField("avatar_url")
    private String avatarUrl;

    private Integer points; // 积分，默认0

    @TableField("weekly_checkin_count")
    private Integer weeklyCheckinCount;
}
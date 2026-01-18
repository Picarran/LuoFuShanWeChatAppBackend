package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("merchant")
public class Merchant extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("resource_id")
    private Long resourceId;

    private String name;
    private String type;

    private String username;
    private String password;

    @TableField("contact_name")
    private String contactName;
    @TableField("contact_phone")
    private String contactPhone;

    private String address;

    private Integer status;
}

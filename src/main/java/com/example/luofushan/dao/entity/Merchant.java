package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("merchant")
public class Merchant extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "resource_id", updateStrategy = FieldStrategy.IGNORED)
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

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

    private String username;

    private String password; 
    private String name;

    private String type;

    @TableField("resource_id")
    private Long resourceId;

    private Integer status;
}

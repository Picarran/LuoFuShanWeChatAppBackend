package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mall_item")
public class MallItem extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField("points_cost")
    private Integer pointsCost;

    @TableField("cover_img")
    private String coverImg;
}

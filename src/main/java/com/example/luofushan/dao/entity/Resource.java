package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.luofushan.dao.BaseDO;
import lombok.Data;


@Data
@TableName("resource")
public class Resource extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String type; // 景点、住宿、餐饮、商家

    private String name;

    @TableField("cover_img")
    private String coverImg;

    private Double latitude;

    private Double longitude;

    @TableField("hot_score")
    private Integer hotScore;

    @TableField("content_json")
    private String contentJson;
}
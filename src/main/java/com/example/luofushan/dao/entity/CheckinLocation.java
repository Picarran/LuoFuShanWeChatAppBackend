package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("checkin_location")
public class CheckinLocation extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    private Integer score;

    private Long todayHasCheckin;

    private String coverImg;
}

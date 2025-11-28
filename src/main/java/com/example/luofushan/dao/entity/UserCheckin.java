package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("user_checkin")
public class UserCheckin extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id; // 记录ID

    private Long userId; // 用户ID

    private Long locationId; // 景点ID

    private LocalDateTime checkinTime; // 打卡时间
}

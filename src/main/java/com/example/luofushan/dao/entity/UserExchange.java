package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_exchange")
public class UserExchange extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("item_id")
    private Long itemId;

    private String code;

    @TableField("is_redeemed")
    private Integer isRedeemed;

    @TableField("place_name")
    private String placeName;

    @TableField("redeemed_at")
    private LocalDateTime redeemedAt;

    @TableField("merchant_id")
    private Long merchantId;  // 核销商家ID
}

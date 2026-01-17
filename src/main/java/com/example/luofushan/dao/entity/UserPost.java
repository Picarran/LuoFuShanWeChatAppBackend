package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "user_post", autoResultMap = true)
public class UserPost extends BaseDO{

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long locationId;

    private String content;

    // 避免 images当成字符串存进去
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private Integer likeCount;

    private Integer commentCount;

    private LocalDateTime postTime;

}
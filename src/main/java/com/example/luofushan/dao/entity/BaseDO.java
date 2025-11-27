package com.example.luofushan.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class BaseDO {

    /**
     * 创建时间
     * 在执行 INSERT 操作时，自动给该字段赋值
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 修改时间
     * 在执行 INSERT UPDATE 操作时，自动给该字段赋值
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer delflag;
}
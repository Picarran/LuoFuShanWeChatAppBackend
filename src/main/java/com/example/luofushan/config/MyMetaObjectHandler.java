package com.example.luofushan.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // LocalDateTime 类型
        strictInsertFill(metaObject, "createdAt", LocalDateTime::now, LocalDateTime.class);
        strictInsertFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);
        strictInsertFill(metaObject, "delflag", () -> 0, Integer.class);
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);
    }
}
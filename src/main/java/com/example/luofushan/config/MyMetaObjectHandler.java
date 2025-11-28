package com.example.luofushan.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject){
        strictInsertFill(metaObject, "createdAt", Date::new, Date.class);
        strictInsertFill(metaObject, "updatedAt", Date::new, Date.class);
        strictInsertFill(metaObject, "delflag", () -> 0, Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject){
        strictUpdateFill(metaObject, "updatedAt", Date::new, Date.class);
    }
}
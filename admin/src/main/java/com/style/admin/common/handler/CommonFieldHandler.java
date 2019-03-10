package com.style.admin.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 公共字段，自动填充值
 */
@Component
public class CommonFieldHandler implements MetaObjectHandler {

    private final static String CREATE_DATE = "createDate";

    private final static String UPDATE_DATE = "updateDate";

    @Override
    public void insertFill(MetaObject metaObject) {

        Date date = new Date();

        //创建时间
        setFieldValByName(CREATE_DATE, date, metaObject);
        //更新时间
        setFieldValByName(UPDATE_DATE, date, metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //更新时间
        setFieldValByName(UPDATE_DATE, new Date(), metaObject);
    }
}
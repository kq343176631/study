package com.style.web;

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

    private final static String CREATE_BY = "createBy";

    private final static String UPDATE_DATE = "updateDate";

    private final static String UPDATE_BY = "updateBy";

    private final static String DEL_FLAG = "delFlag";

    @Override
    public void insertFill(MetaObject metaObject) {
        //User user = UserUtils.getLoginUser();
        Date date = new Date();
        /*if (user == null) {
            return;
        }*/
        //创建者
        setFieldValByName(CREATE_BY, "testUser", metaObject);
        //创建时间
        setFieldValByName(CREATE_DATE, date, metaObject);

        //更新者
        setFieldValByName(UPDATE_BY, "testUser", metaObject);
        //更新时间
        setFieldValByName(UPDATE_DATE, date, metaObject);
        setFieldValByName(DEL_FLAG, "0", metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //更新者
        setFieldValByName(UPDATE_BY, "testUser", metaObject);
        //更新时间
        setFieldValByName(UPDATE_DATE, new Date(), metaObject);
    }
}
package com.style.admin.modules.sys.dao;

import com.style.admin.modules.sys.entity.SysLanguage;
import com.style.common.dao.BaseDao;
import com.style.mybatis.annotation.MyBatisPlus;

/**
 * 国际化
 */
@MyBatisPlus
public interface SysLanguageDao extends BaseDao<SysLanguage> {

    SysLanguage getLanguage(SysLanguage entity);

    void updateLanguage(SysLanguage entity);

}
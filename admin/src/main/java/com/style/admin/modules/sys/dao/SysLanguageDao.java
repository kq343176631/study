package com.style.admin.modules.sys.dao;

import com.style.admin.modules.sys.entity.SysLanguage;
import com.style.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 国际化
 */
@Mapper
public interface SysLanguageDao extends BaseDao<SysLanguage> {

    SysLanguage getLanguage(SysLanguage entity);

    void updateLanguage(SysLanguage entity);

}
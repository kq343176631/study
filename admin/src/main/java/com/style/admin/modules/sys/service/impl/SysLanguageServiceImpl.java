package com.style.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.sys.dao.SysLanguageDao;
import com.style.admin.modules.sys.entity.SysLanguage;
import com.style.admin.modules.sys.service.SysLanguageService;
import com.style.common.service.impl.CrudServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 国际化
 */
@Service
public class SysLanguageServiceImpl extends CrudServiceImpl<SysLanguageDao, SysLanguage> implements SysLanguageService {

    @Override
    protected QueryWrapper<SysLanguage> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public void saveOrUpdate(String tableName, Long tableId, String fieldName, String fieldValue, String language) {
        SysLanguage entity = new SysLanguage();
        entity.setTableName(tableName);
        entity.setTableId(tableId);
        entity.setFieldName(fieldName);
        entity.setFieldValue(fieldValue);
        entity.setLanguage(language);

        //判断是否有数据
        if (baseDao.getLanguage(entity) == null) {
            baseDao.insert(entity);
        } else {
            baseDao.updateLanguage(entity);
        }
    }
}
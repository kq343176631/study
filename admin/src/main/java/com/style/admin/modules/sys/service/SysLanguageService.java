package com.style.admin.modules.sys.service;

import com.style.admin.modules.sys.entity.SysLanguage;
import com.style.common.service.CrudService;

/**
 * 国际化
 */
public interface SysLanguageService extends CrudService<SysLanguage> {

    /**
     * 保存或更新
     *
     * @param tableName  表名
     * @param tableId    表主键
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param language   语言
     */
    void saveOrUpdate(String tableName, Long tableId, String fieldName, String fieldValue, String language);
}


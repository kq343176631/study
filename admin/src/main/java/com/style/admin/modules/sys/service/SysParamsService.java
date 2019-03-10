package com.style.admin.modules.sys.service;

import com.style.admin.modules.sys.entity.SysParamsEntity;
import com.style.common.service.BaseService;
import com.style.common.service.CrudService;

/**
 * 参数管理
 */
public interface SysParamsService extends CrudService<SysParamsEntity> {

    void delete(Long[] ids);

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param paramCode  参数编码
     */
    String getValue(String paramCode);

    /**
     * 根据参数编码，获取value的Object对象
     * @param paramCode  参数编码
     * @param clazz  Object对象
     */
    <T> T getValueObject(String paramCode, Class<T> clazz);

    /**
     * 根据参数编码，更新value
     * @param paramCode  参数编码
     * @param paramValue  参数值
     */
    int updateValueByCode(String paramCode, String paramValue);
}

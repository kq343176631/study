package com.style.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.sys.dao.SysDictDao;
import com.style.admin.modules.sys.entity.SysDict;
import com.style.admin.modules.sys.service.SysDictService;
import com.style.common.service.impl.CrudServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 数据字典
 */
@Service
public class SysDictServiceImpl extends CrudServiceImpl<SysDictDao, SysDict> implements SysDictService {

    @Override
    protected QueryWrapper<SysDict> getWrapper(Map<String, Object> params) {
        String pid = (String) params.get("pid");
        String dictType = (String) params.get("dictType");
        String dictName = (String) params.get("dictName");
        String dictValue = (String) params.get("dictValue");

        QueryWrapper<SysDict> wrapper = new QueryWrapper<>();
        wrapper.eq(pid != null, "pid", Long.parseLong(pid));
        wrapper.eq(StringUtils.isNotBlank(dictType), "dict_type", dictType);
        wrapper.like(StringUtils.isNotBlank(dictName), "dict_name", dictName);
        wrapper.like(StringUtils.isNotBlank(dictValue), "dict_value", dictValue);

        return wrapper;
    }

}
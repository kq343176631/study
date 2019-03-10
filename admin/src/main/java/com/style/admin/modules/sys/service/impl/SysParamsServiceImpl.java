package com.style.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.sys.dao.SysParamsDao;
import com.style.admin.modules.sys.entity.SysParams;
import com.style.admin.modules.sys.service.SysParamsService;
import com.style.common.constant.ErrorCode;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.exception.ValidateException;
import com.style.common.service.impl.CrudServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 参数管理
 */
@Service
public class SysParamsServiceImpl extends CrudServiceImpl<SysParamsDao, SysParams> implements SysParamsService {

    //@Autowired
    //private SysParamsRedis sysParamsRedis;

    protected QueryWrapper<SysParams> getWrapper(Map<String, Object> params) {
        String paramCode = (String) params.get("paramCode");

        QueryWrapper<SysParams> wrapper = new QueryWrapper<>();
        wrapper.eq("param_type", 1);
        wrapper.like(StringUtils.isNotBlank(paramCode), "param_code", paramCode);

        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        //删除Redis数据
        List<String> paramCodeList = baseDao.getParamCodeList(ids);
        String[] paramCodes = paramCodeList.toArray(new String[paramCodeList.size()]);
        //sysParamsRedis.delete(paramCodes);

        //逻辑删除
        deleteByIds(Arrays.asList(ids));
    }

    @Override
    public String getValue(String paramCode) {
//        String paramValue = sysParamsRedis.get(paramCode);
//        if(paramValue == null){
//            paramValue = baseDao.getValueByCode(paramCode);
//
//            sysParamsRedis.set(paramCode, paramValue);
//        }
//        return paramValue;
        return null;
    }

    @Override
    public <T> T getValueObject(String paramCode, Class<T> clazz) {
        String paramValue = getValue(paramCode);
        if (StringUtils.isNotBlank(paramValue)) {
            return JsonMapper.fromJson(paramValue, clazz);
        }

        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.PARAMS_GET_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateValueByCode(String paramCode, String paramValue) {
        return baseDao.updateValueByCode(paramCode, paramValue);
    }

}
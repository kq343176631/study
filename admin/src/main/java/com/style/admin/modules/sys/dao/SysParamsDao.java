package com.style.admin.modules.sys.dao;

import com.style.admin.modules.sys.entity.SysParams;
import com.style.common.dao.BaseDao;
import com.style.mybatis.annotation.MyBatisPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 参数管理
 */
@MyBatisPlus
public interface SysParamsDao extends BaseDao<SysParams> {
    /**
     * 根据参数编码，查询value
     *
     * @param paramCode 参数编码
     * @return 参数值
     */
    String getValueByCode(String paramCode);

    /**
     * 获取参数编码列表
     *
     * @param ids ids
     * @return 返回参数编码列表
     */
    List<String> getParamCodeList(Long[] ids);

    /**
     * 根据参数编码，更新value
     *
     * @param paramCode  参数编码
     * @param paramValue 参数值
     */
    int updateValueByCode(@Param("paramCode") String paramCode, @Param("paramValue") String paramValue);
}

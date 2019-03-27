package com.style.admin.modules.security.dao;

import com.style.admin.modules.security.entity.SysUserTokenEntity;
import com.style.common.dao.BaseDao;
import com.style.mybatis.annotation.MyBatisPlus;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户Token
 */
@MyBatisPlus
public interface SysUserTokenDao extends BaseDao<SysUserTokenEntity> {

    SysUserTokenEntity getByToken(String token);

    SysUserTokenEntity getByUserId(Long userId);

    void updateToken(@Param("userId") Long userId, @Param("token") String token);
}

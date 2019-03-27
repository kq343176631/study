package com.style.admin.modules.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.security.dao.SysUserTokenDao;
import com.style.admin.modules.security.entity.SysUserTokenEntity;
import com.style.admin.modules.security.oauth2.TokenGenerator;
import com.style.admin.modules.security.service.SysUserTokenService;
import com.style.common.constant.Constants;
import com.style.common.model.Result;
import com.style.common.service.impl.CrudServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SysUserTokenServiceImpl extends CrudServiceImpl<SysUserTokenDao,SysUserTokenEntity> implements SysUserTokenService {
    /**
     * 12小时后过期
     */
    private final static int EXPIRE = 3600 * 12;

    @Override
    protected QueryWrapper<SysUserTokenEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public Result createToken(Long userId) {
        //用户token
        String token;

        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

        //判断是否生成过token
        SysUserTokenEntity tokenEntity = baseDao.getByUserId(userId);
        if (tokenEntity == null) {
            //生成一个token
            token = TokenGenerator.generateValue();

            tokenEntity = new SysUserTokenEntity();
            tokenEntity.setUserId(userId);
            tokenEntity.setToken(token);
            tokenEntity.setUpdateDate(now);
            tokenEntity.setExpireDate(expireTime);

            //保存token
            this.save(tokenEntity);
        } else {
            //判断token是否过期
            if (tokenEntity.getExpireDate().getTime() < System.currentTimeMillis()) {
                //token过期，重新生成token
                token = TokenGenerator.generateValue();
            } else {
                token = tokenEntity.getToken();
            }

            tokenEntity.setToken(token);
            tokenEntity.setUpdateDate(now);
            tokenEntity.setExpireDate(expireTime);

            //更新token
            this.updateById(tokenEntity);
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put(Constants.TOKEN_HEADER, token);
        map.put("expire", EXPIRE);
        return new Result(map);
    }

    @Override
    public void logout(Long userId) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //修改token
        baseDao.updateToken(userId, token);
    }
}
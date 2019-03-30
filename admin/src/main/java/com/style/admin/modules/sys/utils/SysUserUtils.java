package com.style.admin.modules.sys.utils;

import com.style.admin.modules.security.authc.LoginInfo;
import com.style.admin.modules.sys.entity.SysMenu;
import com.style.admin.modules.sys.entity.SysRole;
import com.style.admin.modules.sys.entity.SysUser;
import com.style.admin.modules.sys.service.SysUserService;
import com.style.cache.CacheUtils;
import com.style.common.constant.Constants;
import com.style.utils.core.SpringUtils;
import com.style.utils.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.List;

public class SysUserUtils {




    /**
     * 用户service
     */
    private static SysUserService sysUserService = SpringUtils.getBean(SysUserService.class);

    public static List<SysMenu> getUserMenuList() {
        return null;
    }

    /**
     * 需要查询数据库
     *
     * @param loginName loginName
     * @return SysUser SysUser
     */
    public static SysUser getUserByLoginName(String loginName) {
        if (StringUtils.isBlank(loginName)) {
            return null;
        }
        SysUser sysUser = (SysUser) CacheUtils.get(Constants.SYS_USER_CACHE,
                Constants.SYS_USER_CACHE_KEY_PREFIX+loginName);
        if (sysUser == null) {
            sysUser = sysUserService.getUserByLoginName(loginName);
            if (sysUser != null) {
                CacheUtils.put(Constants.SYS_USER_CACHE,
                        Constants.SYS_USER_CACHE_KEY_PREFIX+loginName, sysUser);
            }

        }
        return sysUser;
    }

    /**
     * 获取授权主要对象
     */
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 获取当前会话
     */
    public static Session getSession() {
        Subject subject = getSubject();
        Session session = subject.getSession(false);
        if(session==null){
            session = subject.getSession();
        }
        return session;
    }

    public static SysUser getLoginUser(){

        return null;
    }

    public static LoginInfo getLoginUserPrincipal(){
        return (LoginInfo)getSubject().getPrincipal();
    }

    public static void updateLoginInfo(SysUser user) {

    }

    public static SysUser getUserById(String id) {
        return null;
    }

    public static List<SysRole> getSysRoleList(String loginName) {

        return null;
    }

    public static AuthorizationInfo getAuthorizationInfo(String loginName) {

        return null;
    }
}

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
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.List;

public class SysUserUtils {

    /**
     * 用户service
     */
    private static SysUserService sysUserService = SpringUtils.getBean(SysUserService.class);

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
        if (session == null) {
            session = subject.getSession();
        }
        return session;
    }

    /**
     * 获取登录用户的身份信息
     */
    public static LoginInfo getUserPrincipal() {
        Object principal = getSubject().getPrincipal();
        if (principal != null) {
            return (LoginInfo) principal;
        }
        return null;
    }

    /**
     * 获取当前登录用户
     */
    public static SysUser getLoginUser() {
        LoginInfo loginInfo = getUserPrincipal();
        if (loginInfo != null) {
            return getUserByLoginName(loginInfo.getLoginName());
        }
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
                Constants.SYS_USER_CACHE_KEY_PREFIX + loginName);
        if (sysUser == null) {
            sysUser = sysUserService.getUserByLoginName(loginName);
            if (sysUser != null) {
                CacheUtils.put(Constants.SYS_USER_CACHE,
                        Constants.SYS_USER_CACHE_KEY_PREFIX + loginName, sysUser);
            }

        }
        return sysUser;
    }

    @SuppressWarnings("all")
    public static List<SysRole> getSysRoleList(String loginName) {
        if (StringUtils.isBlank(loginName)) {
            return null;
        }

        List<SysRole> roleList = (List<SysRole>) CacheUtils.get(Constants.ROLE_LIST_CACHE,
                Constants.ROLE_LIST_CACHE_KEY_PREFIX + loginName);
        if (roleList == null) {
            roleList = sysUserService.getSysRoleList(loginName);
            if (roleList != null) {
                CacheUtils.put(Constants.ROLE_LIST_CACHE,
                        Constants.ROLE_LIST_CACHE_KEY_PREFIX + loginName, roleList);
            }

        }
        return roleList;
    }

    @SuppressWarnings("all")
    public static List<SysMenu> getUserMenuList(String loginName) {
        if (StringUtils.isBlank(loginName)) {
            return null;
        }

        List<SysMenu> menuList = (List<SysMenu>) CacheUtils.get(Constants.MENU_LIST_CACHE,
                Constants.MENU_LIST_CACHE_KEY_PREFIX + loginName);
        if (menuList == null) {
            menuList = sysUserService.getSysMenuList(loginName);
            if (menuList != null) {
                CacheUtils.put(Constants.MENU_LIST_CACHE,
                        Constants.MENU_LIST_CACHE_KEY_PREFIX + loginName, menuList);
            }

        }
        return menuList;
    }
}

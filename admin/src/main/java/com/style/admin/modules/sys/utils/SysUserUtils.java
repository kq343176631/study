package com.style.admin.modules.sys.utils;

import com.style.admin.modules.sys.entity.SysMenu;
import com.style.admin.modules.sys.entity.SysUser;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.List;

public class SysUserUtils {

    public static List<SysMenu> getUserMenuList() {
        return null;
    }

    /**
     * 需要查询数据库
     * @param loginName loginName
     * @return SysUser SysUser
     */
    public static SysUser getUserByLoginName(String loginName) {

        return null;
    }

    public static Subject getSubject() {

        return null;
    }

    public static Session getSession() {
        return null;
    }

    public static void updateLoginInfo(SysUser user) {
    }

    public static SysUser get(String id) {
        return null;
    }
}

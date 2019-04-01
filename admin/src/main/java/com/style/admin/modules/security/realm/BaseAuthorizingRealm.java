package com.style.admin.modules.security.realm;

import com.style.admin.modules.security.authc.FormToken;
import com.style.admin.modules.security.authc.LoginInfo;
import com.style.admin.modules.security.session.SessionDAO;
import com.style.admin.modules.sys.entity.SysMenu;
import com.style.admin.modules.sys.entity.SysRole;
import com.style.admin.modules.sys.entity.SysUser;
import com.style.admin.modules.sys.utils.SysUserUtils;
import com.style.cache.CacheUtils;
import com.style.common.constant.Constants;
import com.style.utils.collect.ListUtils;
import com.style.utils.core.GlobalUtils;
import com.style.utils.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class BaseAuthorizingRealm extends AuthorizingRealm {

    private SessionDAO sessionDAO;

    public BaseAuthorizingRealm() {
        this.setCachingEnabled(false);
        this.setAuthenticationTokenClass(FormToken.class);
    }

    /**
     * 统一授权方法
     */
    @Override
    protected final AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object principal = principals.getPrimaryPrincipal();
        if (principal == null) {
            return null;
        }
        LoginInfo loginInfo = (LoginInfo) principal;

        this.HandleMultiAccountLogin(loginInfo);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 添加用户角色信息
        List<SysRole> sysRoleList = SysUserUtils.getSysRoleList(loginInfo.getLoginName());
        if (ListUtils.isNotEmpty(sysRoleList)) {
            for (SysRole role : sysRoleList) {
                info.addRole(role.getName());
            }
        }
        // 添加用权限色信息
        List<SysMenu> sysMenuList = SysUserUtils.getUserMenuList(loginInfo.getLoginName());
        if (ListUtils.isNotEmpty(sysMenuList)) {
            for (SysMenu menu : sysMenuList) {
                if (StringUtils.isNotBlank(menu.getPermission())) {
                    // 添加基于String的权限信息
                    for (String permission : StringUtils.split(menu.getPermission(), ",")) {
                        info.addStringPermission(permission);
                    }
                }
            }
        }
        return info;
    }

    /**
     * 获取授权信息
     */
    @Override
    protected final AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
        LoginInfo loginInfo = (LoginInfo) principals.getPrimaryPrincipal();
        if (loginInfo == null) {
            return null;
        }
        AuthorizationInfo info = (AuthorizationInfo) CacheUtils.get(Constants.AUTH_INFO_CACHE,
                Constants.AUTH_INFO_CACHE_KEY_PREFIX + loginInfo.getLoginName());
        if (info == null) {
            info = doGetAuthorizationInfo(principals);
            if (info != null) {
                CacheUtils.put(Constants.AUTH_INFO_CACHE,
                        Constants.AUTH_INFO_CACHE_KEY_PREFIX + loginInfo.getLoginName(), info);
            }
        }
        return info;
    }

    /**
     * 登录成功后回调
     */
    public final void doAuthOnLoginSuccess(PrincipalCollection principals) {
        getAuthorizationInfo(principals);
    }

    /**
     * 处理账号同时登录问题
     */
    private void HandleMultiAccountLogin(LoginInfo loginInfo) {
        if (Constants.TRUE.equals(GlobalUtils.getProperty("user.multiAccountLogin"))) {
            return;
        }
        // 查询在线会话
        Collection<Session> sessions = this.sessionDAO.getActiveSessions(false, loginInfo, SysUserUtils.getSession());
        if (sessions.size() > 0) {
            // 如果是登录进来的，则踢出已在线用户
            if (SysUserUtils.getSubject().isAuthenticated()) {
                for (Session session : sessions) {
                    this.sessionDAO.delete(session);
                }
            } else {
                // 记住我进来的，并且当前用户已登录，则退出当前用户提示信息。
                SysUserUtils.getSubject().logout();
                throw new AuthenticationException("msg:账号已在其它地方登录，请重新登录。");
            }
        }
    }

    public void setSessionDAO(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }

    /**
     * 创建认证信息
     */
    public SimpleAuthenticationInfo getAuthenticationInfo(SysUser user, Map<String, Object> params) {
        // 构建身份信息
        LoginInfo info = new LoginInfo(user.getId(),
                user.getLoginName(),
                params);
        return new SimpleAuthenticationInfo(info, Constants.EMPTY, this.getName());
    }
}

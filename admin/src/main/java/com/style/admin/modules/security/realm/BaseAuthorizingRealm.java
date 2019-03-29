package com.style.admin.modules.security.realm;

import com.style.admin.modules.security.authc.FormToken;
import com.style.admin.modules.security.authc.LoginInfo;
import com.style.admin.modules.security.session.SessionDAO;
import com.style.admin.modules.sys.entity.SysMenu;
import com.style.admin.modules.sys.entity.SysUser;
import com.style.admin.modules.sys.utils.UserUtils;
import com.style.common.constant.Constants;
import com.style.utils.core.GlobalUtils;
import com.style.utils.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;

import javax.servlet.http.HttpServletRequest;
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
        LoginInfo loginInfo = (LoginInfo) getAvailablePrincipal(principals);
        if (loginInfo == null) {
            return null;
        }
        this.HandleMultiAccountLogin(loginInfo);
        // 获取当前已登录的用户
        SysUser user = UserUtils.getUserByLoginName(loginInfo.getName());
        if (user != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            List<SysMenu> list = UserUtils.getUserMenuList();
            for (SysMenu menu : list) {
                if (StringUtils.isNotBlank(menu.getPermission())) {
                    // 添加基于Permission的权限信息
                    for (String permission : StringUtils.split(menu.getPermission(), ",")) {
                        info.addStringPermission(permission);
                    }
                }
            }
            // 添加用户权限
            info.addStringPermission("user");
            // 添加用户角色信息
            /*for (Role role : user.getRoleList()) {
                info.addRole(role.getEnname());
            }*/
            // 更新登录IP和时间
            //getSystemService().updateUserLoginInfo(user);
            // 记录登录日志
            //LogUtils.saveLog(ServletUtils.getRequest(), "系统登录");
            return info;
        } else {
            return null;
        }
    }

    /**
     * 获取授权信息
     */
    @Override
    protected final AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            return null;
        }
        AuthorizationInfo info;
        //info = (AuthorizationInfo) UserUtils.getCache(SysConstant.CACHE_AUTH_INFO);
        info = null;
        if (info == null) {
            info = doGetAuthorizationInfo(principals);
            if (info != null) {
                //UserUtils.putCache(SysConstant.CACHE_AUTH_INFO, info);
            }
        }
        return info;
    }

    /**
     * 处理账号同时登录问题
     */
    private void HandleMultiAccountLogin(LoginInfo loginInfo) {
        if (Constants.TRUE.equals(GlobalUtils.getProperty("user.multiAccountLogin"))) {
            return;
        }
        // 查询在线会话
        Collection<Session> sessions = this.sessionDAO.getActiveSessions(false, loginInfo, UserUtils.getSession());
        if (sessions.size() > 0) {
            // 如果是登录进来的，则踢出已在线用户
            if (UserUtils.getSubject().isAuthenticated()) {
                for (Session session : sessions) {
                    this.sessionDAO.delete(session);
                }
            } else {
                // 记住我进来的，并且当前用户已登录，则退出当前用户提示信息。
                UserUtils.getSubject().logout();
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

    /**
     * 登录回调
     */
    public void onLoginSuccess(LoginInfo loginInfo, HttpServletRequest request) {

    }

    /**
     * 退出回调
     */
    public void onLogoutSuccess(LoginInfo loginInfo, HttpServletRequest request) {

    }
}

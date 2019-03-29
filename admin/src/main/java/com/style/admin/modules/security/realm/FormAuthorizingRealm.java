package com.style.admin.modules.security.realm;

import com.style.admin.modules.security.authc.FormToken;
import com.style.admin.modules.security.authc.LoginInfo;
import com.style.admin.modules.sys.entity.User;
import com.style.admin.modules.sys.utils.UserUtils;
import com.style.cache.CacheUtils;
import com.style.common.constant.Constants;
import com.style.utils.codec.EncodeUtils;
import com.style.utils.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ByteSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 系统安全认证实现类
 */
public class FormAuthorizingRealm extends BaseAuthorizingRealm {

    public FormAuthorizingRealm() {
        super();
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(Constants.HASH_ALGORITHM);
        matcher.setHashIterations(Constants.HASH_INTERATIONS);
        this.setCredentialsMatcher(matcher);
    }

    /**
     * 表单登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        FormToken formToken = (FormToken) token;
        // 检查验证码是否正确。
        this.checkValidateCode(formToken);

        // 登录次数超过3次，则5分钟后才能继续登录。
        if (getLoginTimes(formToken.getUsername()) > 3) {
            //
        }

        // 检查账号是否被冻结、停用、删除。
        User user = UserUtils.getUserByLoginName(formToken.getUsername());
        if (user == null) {
            throw new AuthenticationException("User info is empty !!!");
        }
        /*if (!"0".equals(user.getStatus())) {
            throw new AuthenticationException("This loginName is deleted or freeze or disabled !!!");
        }*/
        return this.getAuthenticationInfo(user, formToken.getParams());
    }

    @Override
    public SimpleAuthenticationInfo getAuthenticationInfo(User user, Map<String, Object> params) {
        SimpleAuthenticationInfo info = super.getAuthenticationInfo(user, params);
        // 设置用户凭证
        String credential = user.getPassword();
        if (StringUtils.isBlank(credential)) {
            throw new AuthorizationException("User credential is not empty !!!");
        }
        byte[] salt = EncodeUtils.decodeHex(credential.substring(0, 16));
        // 委托给凭证比较器：验证密码是否正确
        info.setCredentials(credential.substring(16));
        info.setCredentialsSalt(ByteSource.Util.bytes(salt));
        return info;
    }

    @Override
    public void onLoginSuccess(LoginInfo loginInfo, HttpServletRequest request) {
        super.onLoginSuccess(loginInfo, request);
        // 更新登录IP、时间、会话ID等
        User user = UserUtils.getUserByLoginName(loginInfo.getLoginName());
        UserUtils.updateLoginInfo(user);
        // 记录用户登录日志
        //LogUtils.saveLog(user, request, "系统登录", Log.TYPE_LOGIN_LOGOUT);
    }

    @Override
    public void onLogoutSuccess(LoginInfo loginInfo, HttpServletRequest request) {
        super.onLogoutSuccess(loginInfo, request);
        // 记录用户退出日志
        User user = UserUtils.getUserByLoginName(loginInfo.getLoginName());
        //LogUtils.saveLog(user, request, "系统退出", Log.TYPE_LOGIN_LOGOUT);
    }

    /**
     * 判断验证码是否正确
     *
     * @param formToken formToken
     */
    private void checkValidateCode(FormToken formToken) {
        Session session = UserUtils.getSession();
        String validCode;
        if (session != null) {
            validCode = (String) session.getAttribute("validateCode");
            if (formToken.getCaptcha() == null || !formToken.getCaptcha().toUpperCase().equals(validCode)) {
                throw new AuthenticationException("验证码错误，请重试。。。");
            }
            session.removeAttribute("validateCode");
        }
    }

    /**
     * 获取登录失败次数
     */
    @SuppressWarnings("unchecked")
    private Integer getLoginTimes(String loginName) {
        Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
        return loginFailMap.get(loginName);
    }

}

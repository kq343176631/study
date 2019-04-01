package com.style.admin.modules.security.realm;

import com.style.admin.modules.security.authc.FormToken;
import com.style.admin.modules.sys.entity.SysUser;
import com.style.admin.modules.sys.utils.SysUserUtils;
import com.style.cache.CaffeineUtils;
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

import java.util.Map;

/**
 * 系统安全认证实现类
 */
public class FormAuthorizingRealm extends BaseAuthorizingRealm {

    private static final int HASH_INTERATIONS = 1024;

    private static final String HASH_ALGORITHM = "SHA-1";

    public FormAuthorizingRealm() {
        super();
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(HASH_ALGORITHM);
        matcher.setHashIterations(HASH_INTERATIONS);
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

        // 登录次数超过3次，则3分钟后才能继续登录。
        if (getLoginTimes(formToken.getUsername()) > 3) {
            throw new AuthenticationException("Password error 3 times , It takes three minutes to log in !!!");
        }

        // 检查账号是否被冻结、停用、删除。
        SysUser user = SysUserUtils.getUserByLoginName(formToken.getUsername());
        if (user == null) {
            throw new AuthenticationException("User info is empty !!!");
        }
        if (user.getStatus() != 0) {
            throw new AuthenticationException("This loginName is deleted or freeze or disabled !!!");
        }
        return this.getAuthenticationInfo(user, formToken.getParams());
    }

    @Override
    public SimpleAuthenticationInfo getAuthenticationInfo(SysUser user, Map<String, Object> params) {
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

    /**
     * 判断验证码是否正确
     */
    private void checkValidateCode(FormToken formToken) {
        Session session = SysUserUtils.getSession();
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
        return (Integer) CaffeineUtils.get(Constants.LOGIN_FAIL_TIMES_CACHE,loginName);
    }

}

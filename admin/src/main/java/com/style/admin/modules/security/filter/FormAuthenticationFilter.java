package com.style.admin.modules.security.filter;

import com.style.admin.modules.log.entity.SysLogLogin;
import com.style.admin.modules.log.enums.OperateStatusEnum;
import com.style.admin.modules.log.enums.OperateTypeEnum;
import com.style.admin.modules.log.utils.SysLogUtils;
import com.style.admin.modules.security.authc.FormToken;
import com.style.admin.modules.security.authc.LoginInfo;
import com.style.admin.modules.security.realm.BaseAuthorizingRealm;
import com.style.admin.modules.sys.entity.SysUser;
import com.style.admin.modules.sys.utils.RedirectUtils;
import com.style.admin.modules.sys.utils.SysUserUtils;
import com.style.cache.CaffeineUtils;
import com.style.common.constant.Constants;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.model.Result;
import com.style.common.web.servlet.ServletUtils;
import com.style.utils.codec.DesUtils;
import com.style.utils.core.GlobalUtils;
import com.style.utils.lang.ObjectUtils;
import com.style.utils.lang.StringUtils;
import com.style.utils.network.IpUtils;
import com.style.utils.network.UserAgentUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 表单登录（认证）过滤类（必须登陆后才能访问）
 * 判断当前用户（Subject）是否认证
 * 没有认证：则继续判断时当前请求是否为登录请求，是登录请求则进行认证。
 */
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(FormAuthenticationFilter.class);

    // 验证码
    public static final String DEFAULT_CAPTCHA_PARAM = "validCode";

    // 登录返回消息
    public static final String DEFAULT_MESSAGE_PARAM = "message";

    // 记住用户名
    public static final String DEFAULT_REMEMBER_USER_CODE_PARAM = "rememberUserCode";

    // 安全认证类
    private BaseAuthorizingRealm authorizingRealm;

    // 记住用户名Cookie
    private Cookie rememberUserCodeCookie;

    /**
     * 构造方法
     */
    public FormAuthenticationFilter() {
        super();
        rememberUserCodeCookie = new SimpleCookie(DEFAULT_REMEMBER_USER_CODE_PARAM);
        rememberUserCodeCookie.setHttpOnly(true);
        rememberUserCodeCookie.setMaxAge(Cookie.ONE_YEAR);
    }

    /**
     * 创建登录授权令牌
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request, response);    // 用户名
        String password = getPassword(request);                // 登录密码
        boolean rememberMe = isRememberMe(request);            // 记住我（记住密码）
        String host = getHost(request);                        // 登录主机
        String captcha = getCaptcha(request);                // 登录验证码
        Map<String, Object> paramMap = ServletUtils.getExtParams(request);    // 登录附加参数
        return new FormToken(username, password.toCharArray(), rememberMe, host, captcha, paramMap);
    }

    /**
     * 获取登录用户名
     */
    protected String getUsername(ServletRequest request, ServletResponse response) {
        String username = super.getUsername(request);
        if (StringUtils.isBlank(username)) {
            username = ObjectUtils.toString(request.getAttribute(getUsernameParam()));
        }
        // 登录用户名解密（解决登录用户名明文传输安全问题）
        String secretKey = GlobalUtils.getProperty("shiro.login-submit.secret-key");
        if (StringUtils.isNotBlank(secretKey)) {
            username = DesUtils.decode(username, secretKey);
            if (StringUtils.isBlank(username)) {
                logger.info("登录账号为空或解码错误.");
            }
        }
        // 登录成功后，判断是否需要记住用户名
        if (WebUtils.isTrue(request, DEFAULT_REMEMBER_USER_CODE_PARAM)) {
            rememberUserCodeCookie.setValue(username);
            rememberUserCodeCookie.saveTo((HttpServletRequest) request, (HttpServletResponse) response);
        } else {
            rememberUserCodeCookie.removeFrom((HttpServletRequest) request, (HttpServletResponse) response);
        }
        return username;
    }

    /**
     * 获取登录密码
     */
    @Override
    protected String getPassword(ServletRequest request) {
        String password = super.getPassword(request);
        if (StringUtils.isBlank(password)) {
            password = ObjectUtils.toString(request.getAttribute(getPasswordParam()));
        }
        // 登录密码解密（解决登录密码明文传输安全问题）
        String secretKey = GlobalUtils.getProperty("shiro.login-submit.secret-key");
        if (StringUtils.isNotBlank(secretKey)) {
            password = DesUtils.decode(password, secretKey);
            if (StringUtils.isBlank(password)) {
                logger.info("登录密码为空或解码错误.");
            }
        }
        return password;
    }

    /**
     * 获取记住我
     */
    @Override
    protected boolean isRememberMe(ServletRequest request) {
        String isRememberMe = WebUtils.getCleanParam(request, getRememberMeParam());
        if (StringUtils.isBlank(isRememberMe)) {
            isRememberMe = ObjectUtils.toString(request.getAttribute(getRememberMeParam()));
        }
        return ObjectUtils.toBoolean(isRememberMe);
    }

    /**
     * 获取请求的客户端主机
     */
    @Override
    protected String getHost(ServletRequest request) {
        return IpUtils.getIpAddr((HttpServletRequest) request);
    }

    /**
     * 获取登录验证码
     */
    protected String getCaptcha(ServletRequest request) {
        String captcha = WebUtils.getCleanParam(request, DEFAULT_CAPTCHA_PARAM);
        if (StringUtils.isBlank(captcha)) {
            captcha = ObjectUtils.toString(request.getAttribute(DEFAULT_CAPTCHA_PARAM));
        }
        // 登录用户名解密（解决登录用户名明文传输安全问题）
        String secretKey = GlobalUtils.getProperty("shiro.login-submit.secret-key");
        if (StringUtils.isNotBlank(secretKey)) {
            captcha = DesUtils.decode(captcha, secretKey);
        }
        return captcha;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        RedirectUtils.redirectToDefaultPath(request, response);
    }

    /**
     * 访问拒绝后，执行登录认证
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("Login page view.");
                }
                //allow them to see the login page
                return true;
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Attempting to access a path which requires authentication.  Forwarding to the "
                        + "Authentication url ["
                        + getLoginUrl() + "]");
            }
            // 此过滤器优先级较高，未登录，则跳转登录页，方便 CAS 登录
            redirectToLogin(request, response);
            return false;
        }
    }

    /**
     * 是否为登录操作（支持GET或CAS登录时传递__login=true参数）
     */
    @Override
    protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
        boolean isLogin = WebUtils.isTrue(request, "__login");
        return super.isLoginRequest(request, response) || isLogin;
    }

    /**
     * 是否为登录操作（支持GET或CAS登录时传递__login=true参数）
     */
    @Override
    protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
        boolean isLogin = WebUtils.isTrue(request, "__login");
        return super.isLoginSubmission(request, response) || isLogin;
    }

    /**
     * 登录成功调用事件
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) {

        // 更新登录IP、时间、会话ID等
        LoginInfo loginInfo = (LoginInfo) subject.getPrincipals();
        SysUser user = SysUserUtils.getUserByLoginName(loginInfo.getLoginName());
        SysUserUtils.updateLoginInfo(user);

        // 登录成功后立即授权
        authorizingRealm.doAuthOnLoginSuccess(subject.getPrincipals());

        // 记录用户登录日志
        SysLogLogin sysLogLogin = new SysLogLogin();
        sysLogLogin.setIp(IpUtils.getIpAddr((HttpServletRequest) request));
        sysLogLogin.setOperation(OperateTypeEnum.LOGIN.value());
        sysLogLogin.setStatus(OperateStatusEnum.SUCCESS.value());
        sysLogLogin.setLoginName(loginInfo.getLoginName());
        sysLogLogin.setUserAgent(UserAgentUtils.getUserAgent((HttpServletRequest) request).toString());
        SysLogUtils.saveSysLogLogin(sysLogLogin);

        // 登录操作如果是Ajax操作，直接返回登录信息字符串。
        Result result = new Result();
        ServletUtils.renderString((HttpServletResponse) response, JsonMapper.toJson(result));

        return false;
    }

    /**
     * 登录失败调用事件
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

        String loginName = (String) token.getPrincipal();
        this.recordLoginFailTimes(loginName);

        // 记录用户登录日志
        SysLogLogin sysLogLogin = new SysLogLogin();
        sysLogLogin.setIp(IpUtils.getIpAddr((HttpServletRequest) request));
        sysLogLogin.setOperation(OperateTypeEnum.LOGIN.value());
        sysLogLogin.setStatus(OperateStatusEnum.FAIL.value());
        sysLogLogin.setLoginName(loginName);
        sysLogLogin.setUserAgent(UserAgentUtils.getUserAgent((HttpServletRequest) request).toString());
        SysLogUtils.saveSysLogLogin(sysLogLogin);

        setFailureAttribute(request, e);

        Result result = new Result();
        ServletUtils.renderString((HttpServletResponse) response, JsonMapper.toJson(result));

        return false;
    }

    /**
     * 记录登录失败次数
     */
    @SuppressWarnings("unchecked")
    private void recordLoginFailTimes(String loginName) {
        Integer loginFailNum = (Integer) CaffeineUtils.get(Constants.LOGIN_FAIL_TIMES_CACHE, loginName);
        if (loginFailNum == null) {
            loginFailNum = 0;
        }
        loginFailNum++;
        CaffeineUtils.put(Constants.LOGIN_FAIL_TIMES_CACHE, loginName, loginFailNum);
    }

    public void setAuthorizingRealm(BaseAuthorizingRealm authorizingRealm) {
        this.authorizingRealm = authorizingRealm;
    }
}
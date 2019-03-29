package com.style.admin.modules.security.realm;

import com.style.admin.modules.security.authc.CasToken;
import com.style.admin.modules.security.authc.LoginInfo;
import com.style.admin.modules.security.cas.CasCreateUser;
import com.style.admin.modules.security.cas.CasOutHandler;
import com.style.admin.modules.sys.entity.User;
import com.style.admin.modules.sys.utils.UserUtils;
import com.style.common.web.servlet.ServletUtils;
import com.style.utils.collect.MapUtils;
import com.style.utils.core.SpringUtils;
import com.style.utils.lang.ObjectUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.Map;

/**
 * 系统安全认证实现类
 */
public class CasAuthorizingRealm extends BaseAuthorizingRealm {

    //private UserService userService;

    //private EmpUserService empUserService;

    // CAS 服务器地址
    private String casServerUrl;

    // CAS 服务器回调地址
    private String casServerCallbackUrl;

    // CAS 令牌验证类
    private TicketValidator ticketValidator;

    private CasOutHandler casOutHandler;

    public CasAuthorizingRealm() {
        super();
        this.setAuthenticationTokenClass(CasToken.class);
    }

    /**
     * 表单登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        CasToken casToken = (CasToken) token;
        // 单点登录登出句柄（登出时注销session）有CAS中央服务器调用
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (casOutHandler.isLogoutRequest(request)) {
            LoginInfo loginInfo = casOutHandler.destroySession(request);
            if (loginInfo != null) {
                this.onLogoutSuccess(loginInfo, request);
            }
            return null;
        }
        // 验证门票
        try {
            String ticket = (String) casToken.getCredentials();
            if (ticketValidator == null) {
                ticketValidator = new Cas20ServiceTicketValidator(casServerUrl);
                ((Cas20ServiceTicketValidator) ticketValidator).setEncoding("UTF-8");
            }

            // 进行登录身份验证
            Assertion casAssertion = ticketValidator.validate(ticket, casServerCallbackUrl);

            AttributePrincipal casPrincipal = casAssertion.getPrincipal();
            casToken.setLoginName(casPrincipal.getName());
            // 登录附加参数
            Map<String, Object> params = MapUtils.newHashMap();
            params.put("ticket", ticket);
            params.putAll(casToken.getParams());
            params.putAll(casPrincipal.getAttributes());
            // 返回认证信息
            return super.getAuthenticationInfo(this.getUserInfo(casToken.getLoginName(), params), params);
        } catch (Exception e) {
            throw new AuthenticationException("Cas login authentication fail !!!");
        }
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        // CAS的Ticket已经在doGetAuthenticationInfo()认证过了，这里就不验证身份了
    }

    @Override
    public void onLoginSuccess(LoginInfo loginInfo, HttpServletRequest request) {
        super.onLoginSuccess(loginInfo, request);

        // 单点登录登出句柄（登录时注入session），在这之前必须获取下授权信息
        String ticket = loginInfo.getParamValue("ticket");
        casOutHandler.recordSession(request, ticket);

        // 更新登录IP、时间、会话ID等
        /*User user = UserUtils.get(loginInfo.getId());
        getUserService().updateUserLoginInfo(user);*/

        // 记录用户登录日志
        //LogUtils.saveLog(user, ServletUtils.getRequest(), "系统登录", Log.TYPE_LOGIN_LOGOUT);
    }

    @Override
    public void onLogoutSuccess(LoginInfo loginInfo, HttpServletRequest request) {
        super.onLogoutSuccess(loginInfo, request);
        // 记录用户退出日志
        User user = UserUtils.get(loginInfo.getId());
        //LogUtils.saveLog(user, request, "系统退出", Log.TYPE_LOGIN_LOGOUT);
    }

    /**
     * 获取登录信息
     */
    private User getUserInfo(String loginName, Map<String, Object> params) {
        User user = UserUtils.getUserByLoginName(loginName);
        if (user == null) {
            // 如果允许客户端创建账号，则创建账号
            if (ObjectUtils.toBoolean(params.get("isAllowClientCreateUser"))) {

                // 获取CAS传递过来的用户属性信息
                //user = new User(CodecUtils.decodeUrl(ObjectUtils.toString(params.get("userCode"))));
                //user.setLoginCode(CodecUtils.decodeUrl(ObjectUtils.toString(params.get("loginCode"))));
                //user.setPassword(CodecUtils.decodeUrl(ObjectUtils.toString(params.get("password"))));
                //user.setUserName(CodecUtils.decodeUrl(ObjectUtils.toString(params.get("userName"))));
                //user.setEmail(CodecUtils.decodeUrl(ObjectUtils.toString(params.get("email"))));
                //user.setMobile(CodecUtils.decodeUrl(ObjectUtils.toString(params.get("mobile"))));
                //user.setPhone(CodecUtils.decodeUrl(ObjectUtils.toString(params.get("phone"))));
                //user.setUserType(CodecUtils.decodeUrl(ObjectUtils.toString(params.get("userType"))));

                // 如果是员工类型，则平台自动创建
                if (User.USER_TYPE_EMPLOYEE.equals(user.getUserType())) {

                    // 保存员工和用户
                    try {
                        //EmpUser empUser = new EmpUser();
                        //empUser.setIsNewRecord(true);
                        // empUser.setMobile(user.getMobile());
                        //empUser.setEmail(user.getEmail());
                        //empUser.setPhone(user.getPhone());
                        // empUser.getEmployee().getCompany().setCompanyCode(CodecUtils
                        //.decodeUrl(ObjectUtils.toString(params.get("companyCode"))));
                        //empUser.getEmployee().getOffice().setOfficeCode(CodecUtils
                        //.decodeUrl(ObjectUtils.toString(params.get("officeCode"))));
                        //getEmpUserService().save(empUser);
                    } catch (ValidationException ve) {
                        throw new AuthenticationException("msg:" + ve.getMessage());
                    }

                    // 重新获取用户登录
                    user = UserUtils.getUserByLoginName(loginName);
                    if (user != null) {
                        return user;
                    }

                }

                // 其它类型，根据项目需要自行创建
                else {
                    try {
                        CasCreateUser casCreateUser = SpringUtils.getBean(CasCreateUser.class);
                        if (casCreateUser != null) {
                            casCreateUser.createUser(user, params);
                        }
                    } catch (NoSuchBeanDefinitionException e) {
                        throw new AuthenticationException("msg:用户 “" + loginName
                                + "”, 类型 “" + user.getUserType() + "” 在本系统中不存在, 请联系管理员.");
                    }
                }
            } else {
                throw new AuthenticationException("msg:用户 “" + loginName + "” 在本系统中不存在, 请联系管理员.");
            }
        }
        return user;
    }

    /*private UserService getUserService() {
        if (userService == null) {
            userService = SpringUtils.getBean(UserService.class);
        }
        return userService;
    }

    private EmpUserService getEmpUserService() {
        if (empUserService == null) {
            empUserService = SpringUtils.getBean(EmpUserService.class);
        }
        return empUserService;
    }*/

    public void setCasOutHandler(CasOutHandler casOutHandler) {
        this.casOutHandler = casOutHandler;
    }

    public void setCasServerUrl(String casServerUrl) {
        this.casServerUrl = casServerUrl;
    }

    public void setCasServerCallbackUrl(String casServerCallbackUrl) {
        this.casServerCallbackUrl = casServerCallbackUrl;
    }
}

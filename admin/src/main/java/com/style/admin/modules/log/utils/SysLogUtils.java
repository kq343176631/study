package com.style.admin.modules.log.utils;

import com.style.admin.modules.log.entity.SysLogError;
import com.style.admin.modules.log.entity.SysLogLogin;
import com.style.admin.modules.log.entity.SysLogOperate;
import com.style.admin.modules.log.service.SysLogErrorService;
import com.style.admin.modules.log.service.SysLogLoginService;
import com.style.admin.modules.log.service.SysLogOperateService;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.web.servlet.ServletUtils;
import com.style.utils.collect.MapUtils;
import com.style.utils.core.SpringUtils;
import com.style.utils.lang.ExceptionUtils;
import com.style.utils.network.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 日志工具类
 */
public class SysLogUtils {

    private static final Logger logger = LoggerFactory.getLogger(SysLogUtils.class);

    /**
     * 错误日志service
     */
    private static final SysLogErrorService sysLogErrorService = SpringUtils.getBean(SysLogErrorService.class);

    /**
     * 登陆日志service
     */
    private static final SysLogLoginService sysLogLoginService = SpringUtils.getBean(SysLogLoginService.class);

    /**
     * 操作日志service
     */
    private static final SysLogOperateService sysLogOperateService = SpringUtils.getBean(SysLogOperateService.class);

    /**
     * 保存系统日志
     */
    public static void saveSysLogError(Exception ex) {

        SysLogError log = new SysLogError();

        HttpServletRequest request = ServletUtils.getHttpServletRequest();

        if (request != null) {

            log.setIp(IpUtils.getIpAddr(request));
            log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            log.setRequestUri(request.getRequestURI());
            log.setRequestMethod(request.getMethod());

            Map<String, String[]> params = ServletUtils.getParameterMap(request);
            if (MapUtils.isNotEmpty(params)) {
                log.setRequestParams(JsonMapper.toJson(params));
            }
            //异常信息
            log.setErrorInfo(ExceptionUtils.getStackTraceAsString(ex));
            //保存
            sysLogErrorService.save(log);
        }

    }

    /**
     * 保存登录日志
     * @param sysLogLogin sysLogLogin
     */
    public static void saveSysLogLogin(SysLogLogin sysLogLogin){

        sysLogLoginService.save(sysLogLogin);

    }

}

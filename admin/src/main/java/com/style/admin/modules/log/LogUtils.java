package com.style.admin.modules.log;

import com.style.common.convert.http.json.JsonMapper;
import com.style.admin.modules.log.entity.SysLog;
import com.style.admin.modules.log.service.SysLogService;
import com.style.common.web.ServletUtils;
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
public class LogUtils {

    private static final Logger logger = LoggerFactory.getLogger(LogUtils.class);

    private static final SysLogService sysLogService = SpringUtils.getBean(SysLogService.class);

    /**
     * 保存系统日志
     */
    public static void saveSysLog(Exception ex) {

        SysLog log = new SysLog();

        HttpServletRequest request = ServletUtils.getHttpServletRequest();

        if (request != null) {

            log.setIp(IpUtils.getIpAddr(request));
            log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            log.setRequestUri(request.getRequestURI());
            log.setRequestMethod(request.getMethod());

            Map<String, String> params = ServletUtils.getParameterMap(request);
            if (MapUtils.isNotEmpty(params)) {
                log.setRequestParams(JsonMapper.toJson(params));
            }
            //异常信息
            log.setErrorInfo(ExceptionUtils.getStackTraceAsString(ex));
            //保存
            sysLogService.insert(log);
        }

    }

}

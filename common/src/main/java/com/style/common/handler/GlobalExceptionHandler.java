package com.style.common.handler;

import com.style.common.constant.ErrorCode;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.exception.ValidateException;
import com.style.common.log.entity.SysLog;
import com.style.common.log.service.SysLogService;
import com.style.common.model.Result;
import com.style.common.web.ServletUtils;
import com.style.utils.collect.MapUtils;
import com.style.utils.core.SpringUtils;
import com.style.utils.lang.ExceptionUtils;
import com.style.utils.network.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private SysLogService sysLogService = SpringUtils.getBean(SysLogService.class);

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(ValidateException.class)
    public Result handleRenException(ValidateException ex) {
        return new Result(ex.getCode(), ex.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        saveLog(ex);
        return new Result(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 保存异常日志
     */
    private void saveLog(Exception ex) {

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

package com.style.common.handler;

import com.style.common.constant.ErrorCode;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.exception.ValidateException;
import com.style.common.log.LogUtils;
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

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(ValidateException.class)
    public Result handleRenException(ValidateException ex) {
        return new Result(ex.getCode(), ex.getMsg());
    }

    /**
     * 处理异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        LogUtils.saveSysLog(ex);
        return new Result(ErrorCode.INTERNAL_SERVER_ERROR);
    }

}

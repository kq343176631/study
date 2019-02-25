package com.style.admin.common.handler;

import com.style.admin.modules.log.LogUtils;
import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;
import com.style.common.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        LogUtils.saveSysLog(ex);
        return new Result(ErrorCode.INTERNAL_SERVER_ERROR);
    }

}

package com.style.common.handler;

import com.style.common.constant.ErrorCode;
import com.style.common.entity.SysLog;
import com.style.common.exception.ValidateException;
import com.style.common.model.Result;
import com.style.common.service.SysLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private SysLogService sysLogService;

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

        //请求相关信息
		/*HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		log.setIp(IpUtils.getIpAddr(request));
		log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
		log.setRequestUri(request.getRequestURI());
		log.setRequestMethod(request.getMethod());
		Map<String, String> params = HttpContextUtils.getParameterMap(request);
		if(MapUtil.isNotEmpty(params)){
			log.setRequestParams(JSON.toJSONString(params));
		}

		//异常信息
		log.setErrorInfo(ExceptionUtils.getErrorStackTrace(ex));*/

        //保存
        sysLogService.save(log);
    }
}

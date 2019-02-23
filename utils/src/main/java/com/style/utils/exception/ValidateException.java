package com.style.utils.exception;

import com.style.utils.MessageUtils;
import com.style.utils.constant.ErrorCode;

/**
 * 验证异常
 */
public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    public ValidateException(int code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public ValidateException(int code, String... params) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public ValidateException(int code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public ValidateException(int code, Throwable e, String... params) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public ValidateException(String msg) {
        super(msg);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public ValidateException(String msg, Throwable e) {
        super(msg, e);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

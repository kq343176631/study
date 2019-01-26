package com.style.common.exception;

import com.style.common.utils.MessageUtils;

public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;

    private String msg;

    public ValidateException(String code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public ValidateException(String code, String... params) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public ValidateException(String code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public ValidateException(String code, Throwable e, String... params) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

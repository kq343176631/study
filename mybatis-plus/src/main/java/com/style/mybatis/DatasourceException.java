package com.style.mybatis;

import com.style.utils.MessageUtils;

public class DatasourceException extends Exception {

    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    public DatasourceException(int code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public DatasourceException(int code, String... params) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public DatasourceException(int code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public DatasourceException(int code, Throwable e, String... params) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public DatasourceException(String msg) {
        super(msg);
        this.code = 500;
        this.msg = msg;
    }

    public DatasourceException(String msg, Throwable e) {
        super(msg, e);
        this.code = 500;
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

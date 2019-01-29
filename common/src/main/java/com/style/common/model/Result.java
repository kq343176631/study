package com.style.common.model;

import com.style.common.constant.ErrorCode;

import java.io.Serializable;

/**
 * Rest Result
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // 编码：0表示成功，其他值表示失败。
    private String code = "0";

    // 消息内容
    private String msg = "成功";

    // 响应数据
    private T data = null;

    public Result() {

    }

    public Result(T data) {
        this.data = data;
    }

    public Result(ErrorCode code) {
        this(code, null);
    }

    public Result(ErrorCode code, T data) {
        this.code = code.getCode();
        this.msg = code.getMsg();
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

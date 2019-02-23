package com.style.utils.model;

import com.style.utils.MessageUtils;

import java.io.Serializable;

/**
 * Rest Result
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // 编码：0表示成功，其他值表示失败。
    private int code = 0;

    // 消息内容
    private String msg = "success";

    // 响应数据
    private T data;

    public Result() {

    }

    public Result(int code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(this.code);
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(T data) {
        this.data = data;
    }

    public Result(int code, T data) {
        this.code = code;
        this.msg = MessageUtils.getMessage(this.code);
        this.data = data;
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

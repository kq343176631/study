package com.style.common.web;

import com.style.common.model.Result;

/**
 * 适用于移动端（REST）
 */
public abstract class WebController extends BaseController {

    protected Result success() {
        return new Result<>();
    }

    protected <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    protected Result error(int code) {
        return new Result<>(code);
    }

    protected <T> Result<T> error(int code, T data) {
        return new Result<>(code, data);
    }

}

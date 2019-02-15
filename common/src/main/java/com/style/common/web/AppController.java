package com.style.common.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.style.common.constant.ErrorCode;
import com.style.common.model.Result;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * 适用于移动端（REST）
 */
public abstract class AppController extends BaseController {

    /**
     * 管理基础路径
     */
    @Value("${adminPath}")
    protected String adminPath;

    /**
     * 前端基础路径
     */
    @Value("${frontPath}")
    protected String frontPath;

    protected Result success() {
        return new Result<>();
    }

    protected <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    protected <T> Result<T> error() {
        return new Result<>(ErrorCode.INTERNAL_SERVER_ERROR, null);
    }

    protected Result error(int code) {
        return new Result<>(code, null);
    }

    protected <T> Result<T> error(int code, T data) {
        return new Result<>(code, data);
    }

}

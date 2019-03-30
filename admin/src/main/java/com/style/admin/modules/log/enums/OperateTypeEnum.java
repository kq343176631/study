package com.style.admin.modules.log.enums;

/**
 * 登录操作枚举
 */
public enum OperateTypeEnum {
    /**
     * 用户登录
     */
    LOGIN(0),
    /**
     * 用户退出
     */
    LOGOUT(1);

    private int value;

    OperateTypeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
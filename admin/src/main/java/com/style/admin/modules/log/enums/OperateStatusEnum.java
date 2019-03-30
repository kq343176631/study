package com.style.admin.modules.log.enums;

/**
 * 操作状态枚举
 */
public enum OperateStatusEnum {
    /**
     * 失败
     */
    FAIL(0),
    /**
     * 成功
     */
    SUCCESS(1);

    private int value;

    OperateStatusEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
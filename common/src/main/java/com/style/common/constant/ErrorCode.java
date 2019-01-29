package com.style.common.constant;

/**
 * 错误编码，由5位数字组成，前2位为模块编码，后3位为业务编码
 * <p>
 * 如：10001（10代表系统模块，001代表业务代码）
 * </p>
 */
public enum ErrorCode {
    /**
     * 系统提示信息
     */
    SUCCESS("0","成功"),
    INTERNAL_SERVER_ERROR("500","服务内部错误"),

    REQUIRED_IDENTIFY_NOT_EXIST("1001", "请求标识对象不存在"),

    OVERSTEP("1002", "数量超出上限"),

    ILLEGAL_PARAMETER("1003", "非法参数"),

    MISMATCH("4001", "请求参数类型不匹配"),

    MISS_PARAM("4002", "缺少必须的参数"),

    RESOURCE_NOT_READABLE("4003", "数据转化错误"),

    RESOURCE_NOT_FOUND("404", "请求的资源不存在"),

    METHOD_NOT_SUPPORTED("405", "不支持的请求方法"),

    MEDIA_TYPE_NOT_ACCEPT("406", "无法接受请求中的媒体类型"),

    MEDIA_TYPE_NOT_SUPPORTED("415", "不支持的媒体类型"),

    SERVER_ERROR("500", "获取数据异常"),

    ACCOUNT_NOT_EXIST("5001", "账号不存在"),

    ACCOUNT_IS_EXIST("5002", "账号已经存在"),

    NOT_LOGIN_ERROR("5003", "账号未登录"),

    PARSE_PROPERTY_ERROR("5004", "属性解析错误"),

    PASSWORD_NOT_MATCH("5005", "密码不正确"),

    MOBILE_IS_EXIST("5006", "手机号已存在"),

    CAPTCHA_ERROR_OR_EXPIRE("5007", "验证码不正确或已过期"),

    UNSUPPORTED_FILE_TYPE("5008", "不支持的文件类型"),

    MODIFY_PROPERTY_IS_NULL("5009", "修改属性为空"),

    FILE_UPLOAD_ERROR("5010", "文件上传出错"),

    DATA_SAVE_EXCEPTION("5011", "数据保存异常"),

    PUSH_MESSAGE_ERROR("5012", "推送消息异常"),

    LOCKED_ACCOUNT_EXCEPTION("5013", "账号已锁定"),

    UNSUPPORTED_PARAMETER_FORMAT("5014", "不支持的参数格式"),

    INCORRECT_PASSWORD_FORMATER_("5015", "密码格式不正确"),

    USER_ACCOUNT_LOGIN_ERROR("5016", "无法登陆,此账户权限不足"),

    USER_CAPTCHA_ERROR("5017", "验证码错误");

    /**
     * 编号
     */
    private String code;

    /**
     * 提示
     */
    private String msg;

    /**
     * 构造函数
     *
     * @param code 编号
     * @param msg  提示
     */
    ErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * get-编号
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * set-编号
     *
     * @param code code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * get-提示
     *
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * set-提示
     *
     * @param msg msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}

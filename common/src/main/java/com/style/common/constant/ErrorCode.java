package com.style.common.constant;

/**
 * 错误编码，由5位数字组成，前2位为模块编码，后3位为业务编码
 * <p>
 * 如：10001（10代表系统模块，001代表业务代码）
 * </p>
 */
public interface ErrorCode {

    //  未经认证
    int UNAUTHORIZED = 401;
    //  没有权限
    int FORBIDDEN = 403;
    // 请求的资源不存在
    int RESOURCE_NOT_FOUND = 404;

    //  内部错误
    int INTERNAL_SERVER_ERROR = 500;
    //  添加失败
    int INSERT_RECORD_ERROR = 501;
    //  删除失败
    int DELETE_RECORD_ERROR = 502;
    //  修改失败
    int UPDATE_RECORD_ERROR = 503;


    //  缺少必备的参数
    int PARAM_MISS = 10001;
    //  参数不能为空
    int PARAM_NOT_NULL = 10002;
    //  参数类型不匹配
    int PARAM_NOT_MATCH = 10003;


    //  数据库中已存在该记录
    int DB_RECORD_EXISTS = 10004;
    //  唯一标识不能为空
    int IDENTIFIER_NOT_NULL = 10005;
    //  验证码错误
    int CAPTCHA_ERROR = 10006;

    //  token不能为空
    int TOKEN_NOT_EMPTY = 10007;
    //  token失效，请重新登录
    int TOKEN_INVALID = 10008;


    //  账号已存在
    int ACCOUNT_EXIST = 10011;
    //  账号不存在
    int ACCOUNT_NOT_EXIST = 10012;
    //  账号未登录
    int ACCOUNT_NOT_LOGIN = 10013;
    //  账号或密码错误
    int ACCOUNT_PASSWORD_ERROR = 10014;
    //  账号已被停用
    int ACCOUNT_DISABLE = 10015;
    //  账号已被锁定
    int ACCOUNT_LOCK = 10016;

}

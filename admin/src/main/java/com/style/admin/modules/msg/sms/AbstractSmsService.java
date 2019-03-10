package com.style.admin.modules.msg.sms;

import com.style.admin.modules.msg.config.SmsConfig;

import java.util.LinkedHashMap;

/**
 * 短信
 */
public abstract class AbstractSmsService {
    /**
     * 短信配置信息
     */
    SmsConfig config;

    /**
     * 发送短信
     *
     * @param mobile 手机号
     * @param params 参数
     */
    public abstract void sendSms(String mobile, LinkedHashMap<String, String> params);

    /**
     * 发送短信
     *
     * @param mobile   手机号
     * @param params   参数
     * @param signName 短信签名
     * @param template 短信模板
     */
    public abstract void sendSms(String mobile, LinkedHashMap<String, String> params, String signName, String template);
}

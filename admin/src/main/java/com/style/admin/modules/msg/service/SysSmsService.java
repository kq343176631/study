package com.style.admin.modules.msg.service;

import com.style.admin.modules.msg.entity.SysSms;
import com.style.common.service.CrudService;

import java.util.LinkedHashMap;

/**
 * 短信
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysSmsService extends CrudService<SysSms> {

    /**
     * 发送短信
     * @param mobile   手机号
     * @param params   短信参数
     */
    void send(String mobile, String params);

    /**
     * 保存短信发送记录
     * @param platform   平台
     * @param mobile   手机号
     * @param params   短信参数
     * @param status   发送状态
     */
    void save(Integer platform, String mobile, LinkedHashMap<String, String> params, Integer status);
}


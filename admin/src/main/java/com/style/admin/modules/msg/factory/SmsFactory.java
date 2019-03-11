package com.style.admin.modules.msg.factory;

import com.style.admin.modules.msg.config.SmsConfig;
import com.style.admin.modules.msg.sms.AbstractSmsService;
import com.style.admin.modules.msg.sms.AliyunSmsService;
import com.style.admin.modules.msg.sms.QcloudSmsService;
import com.style.admin.modules.sys.service.SysParamsService;
import com.style.common.constant.Constants;
import com.style.utils.core.SpringUtils;

/**
 * 短信Factory
 *
 * @author Mark sunlightcs@gmail.com
 */
public class SmsFactory {

    private static SysParamsService sysParamsService;

    static {
        SmsFactory.sysParamsService = SpringUtils.getBean(SysParamsService.class);
    }

    public static AbstractSmsService build() {
        //获取短信配置信息
        SmsConfig config = sysParamsService.getValueObject(Constants.SMS_CONFIG_KEY, SmsConfig.class);

        if (config.getPlatform() == Constants.SmsService.ALIYUN.getValue()) {
            return new AliyunSmsService(config);
        } else if (config.getPlatform() == Constants.SmsService.QCLOUD.getValue()) {
            return new QcloudSmsService(config);
        }

        return null;
    }
}

package com.style.admin.modules.msg.sms;


import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.style.admin.modules.msg.config.SmsConfig;
import com.style.admin.modules.msg.service.SysSmsService;
import com.style.common.constant.Constants;
import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;
import com.style.utils.collect.MapUtils;
import com.style.utils.core.SpringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 腾讯云短信服务
 *
 * @author Mark sunlightcs@gmail.com
 */
public class QcloudSmsService extends AbstractSmsService {

    public QcloudSmsService(SmsConfig config) {
        this.config = config;
    }

    @Override
    public void sendSms(String mobile, LinkedHashMap<String, String> params) {
        this.sendSms(mobile, params, config.getQcloudSignName(), config.getQcloudTemplateId());
    }

    @Override
    public void sendSms(String mobile, LinkedHashMap<String, String> params, String signName, String template) {
        SmsSingleSender sender = new SmsSingleSender(config.getQcloudAppId(), config.getQcloudAppKey());

        //短信参数
        ArrayList<String> paramsList = new ArrayList<>();
        if (MapUtils.isNotEmpty(params)) {
            for (String value : params.values()) {
                paramsList.add(value);
            }
        }
        SmsSingleSenderResult result;
        try {
            result = sender.sendWithParam("86", mobile, Integer.parseInt(template), paramsList, signName, null, null);
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.SEND_SMS_ERROR, e, "");
        }

        int status = Constants.SUCCESS;
        if (result.result != 0) {
            status = Constants.FAIL;
        }

        //保存短信记录
        SysSmsService sysSmsService = SpringUtils.getBean(SysSmsService.class);
        sysSmsService.save(Constants.SmsService.QCLOUD.getValue(), mobile, params, status);

        if (status == Constants.FAIL) {
            throw new ValidateException(ErrorCode.SEND_SMS_ERROR, result.errMsg);
        }
    }
}

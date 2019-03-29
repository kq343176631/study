package com.style.admin.modules.msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.msg.dao.SysSmsDao;
import com.style.admin.modules.msg.entity.SysSms;
import com.style.admin.modules.msg.factory.SmsFactory;
import com.style.admin.modules.msg.service.SysSmsService;
import com.style.admin.modules.msg.sms.AbstractSmsService;
import com.style.common.constant.ErrorCode;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.exception.ValidateException;
import com.style.common.service.impl.CrudServiceImpl;
import com.style.utils.collect.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SysSmsServiceImpl extends CrudServiceImpl<SysSmsDao, SysSms> implements SysSmsService {


    protected QueryWrapper<SysSms> getWrapper(Map<String, Object> params) {
        String mobile = (String) params.get("mobile");
        String status = (String) params.get("status");

        QueryWrapper<SysSms> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(mobile), "mobile", mobile);
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);

        return wrapper;
    }

    @Override
    public void send(String mobile, String params) {
        LinkedHashMap<String, String> map;
        try {
            map = JsonMapper.fromJson(params, LinkedHashMap.class);
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.JSON_FORMAT_ERROR);
        }

        //短信服务
        AbstractSmsService service = SmsFactory.build();
        if (service == null) {
            throw new ValidateException(ErrorCode.SMS_CONFIG);
        }

        //发送短信
        service.sendSms(mobile, map);
    }

    @Override
    public void save(Integer platform, String mobile, LinkedHashMap<String, String> params, Integer status) {
        SysSms sms = new SysSms();
        sms.setPlatform(platform);
        sms.setMobile(mobile);

        //设置短信参数
        if (MapUtils.isNotEmpty(params)) {
            int index = 1;
            for (String value : params.values()) {
                if (index == 1) {
                    sms.setParams1(value);
                } else if (index == 2) {
                    sms.setParams2(value);
                } else if (index == 3) {
                    sms.setParams3(value);
                } else if (index == 4) {
                    sms.setParams4(value);
                }
                index++;
            }
        }

        sms.setStatus(status);

        this.save(sms);
    }
}

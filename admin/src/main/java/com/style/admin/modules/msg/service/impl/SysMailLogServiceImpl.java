package com.style.admin.modules.msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.msg.dao.SysMailLogDao;
import com.style.admin.modules.msg.entity.SysMailLog;
import com.style.admin.modules.msg.service.SysMailLogService;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.service.impl.CrudServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class SysMailLogServiceImpl extends CrudServiceImpl<SysMailLogDao, SysMailLog> implements SysMailLogService {

    protected QueryWrapper<SysMailLog> getWrapper(Map<String, Object> params) {
        String templateId = (String) params.get("templateId");
        String mailTo = (String) params.get("mailTo");
        String status = (String) params.get("status");

        QueryWrapper<SysMailLog> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(templateId), "template_id", templateId);
        wrapper.like(StringUtils.isNotBlank(mailTo), "mail_to", mailTo);
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);

        return wrapper;
    }

    @Override
    public void save(Long templateId, String from, String[] to, String[] cc, String subject, String content, Integer status) {
        SysMailLog log = new SysMailLog();
        log.setTemplateId(templateId);
        log.setMailFrom(from);
        log.setMailTo(JsonMapper.toJson(to));
        if (cc != null) {
            log.setMailCc(JsonMapper.toJson(cc));
        }
        log.setSubject(subject);
        log.setContent(content);
        log.setStatus(status);
        this.save(log);
    }

}
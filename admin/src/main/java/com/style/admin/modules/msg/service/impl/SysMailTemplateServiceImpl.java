package com.style.admin.modules.msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.msg.dao.SysMailTemplateDao;
import com.style.admin.modules.msg.email.EmailUtils;
import com.style.admin.modules.msg.entity.SysMailTemplate;
import com.style.admin.modules.msg.service.SysMailTemplateService;
import com.style.common.constant.ErrorCode;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.exception.ValidateException;
import com.style.common.service.impl.CrudServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysMailTemplateServiceImpl extends CrudServiceImpl<SysMailTemplateDao, SysMailTemplate> implements SysMailTemplateService {

    @Autowired
    private EmailUtils emailUtils;

    @Override
    public QueryWrapper<SysMailTemplate> getWrapper(Map<String, Object> params) {
        String name = (String) params.get("name");

        QueryWrapper<SysMailTemplate> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        return wrapper;
    }

    @Override
    public boolean sendMail(Long id, String mailTo, String mailCc, String params) throws Exception {
        Map<String, Object> map = null;
        try {
            if (StringUtils.isNotEmpty(params)) {
                map = JsonMapper.fromJson(params, Map.class);
            }
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.JSON_FORMAT_ERROR);
        }
        String[] to = new String[]{mailTo};
        String[] cc = StringUtils.isBlank(mailCc) ? null : new String[]{mailCc};

        return emailUtils.sendMail(id, to, cc, map);
    }
}

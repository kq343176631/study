package com.style.admin.modules.msg.service;

import com.style.admin.modules.msg.entity.SysMailTemplateEntity;
import com.style.common.service.CrudService;

/**
 * 邮件模板
 */
public interface SysMailTemplateService extends CrudService<SysMailTemplateEntity> {

    /**
     * 发送邮件
     * @param id           邮件模板ID
     * @param mailTo       收件人
     * @param mailCc       抄送
     * @param params       模板参数
     */
    boolean sendMail(Long id, String mailTo, String mailCc, String params) throws Exception;
}
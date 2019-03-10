package com.style.admin.modules.msg.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.style.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邮件模板
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_mail_template")
public class SysMailTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    private String name;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件正文
     */
    private String content;

}
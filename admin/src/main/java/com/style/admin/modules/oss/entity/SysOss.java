package com.style.admin.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.style.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件上传
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_oss")
public class SysOss extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * URL地址
     */
    private String url;

}
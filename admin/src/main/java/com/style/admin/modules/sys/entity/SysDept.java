package com.style.admin.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.style.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 部门管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dept")
public class SysDept extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 上级ID
     */
    private Long pid;
    /**
     * 所有上级ID，用逗号分开
     */
    private String pids;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
    /**
     * 上级部门名称
     */
    @TableField(exist = false)
    private String parentName;

}
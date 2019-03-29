package com.style.admin.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.style.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /**
     * 父菜单ID，一级菜单为0
     */
    private Long pid;
    /**
     * 菜单名称
     */
    @TableField(exist = false)
    private String name;
    /**
     * 菜单URL
     */
    private String url;
    /**
     * 授权(多个用逗号分隔，如：sys:user:list,sys:user:save)
     */
    private String permissions;
    /**
     * 类型   0：菜单   1：按钮
     */
    private Integer type;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 上级菜单名称
     */
    @TableField(exist = false)
    private String parentName;

    public String getPermission() {

        return null;
    }
}

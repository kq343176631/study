package com.style.admin.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.style.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色菜单关系
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 菜单ID
     */
    private Long menuId;

}

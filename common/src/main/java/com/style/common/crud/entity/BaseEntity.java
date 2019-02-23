package com.style.common.crud.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity
 */
@Data
public abstract class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    protected String id;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    protected String createBy;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    protected Date createDate;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String updateBy;

    /**
     * 更新日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date updateDate;

    /**
     * 删除标记（0：正常；1：删除；2：审核）
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    protected Integer del;

    public BaseEntity() {

    }
}

package com.style.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity
 */
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    protected Long id;

    /**
     * 创建者
     */
    protected Long creator;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    protected Date createDate;

    /**
     * 更新者
     */
    protected Long updater;

    /**
     * 更新日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date updateDate;

    public BaseEntity() {

    }
}

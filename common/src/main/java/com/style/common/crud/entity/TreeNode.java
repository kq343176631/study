package com.style.common.crud.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树节点，所有需要实现树节点的，都需要继承该类
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TreeNode extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 上级ID
     */
    protected String pid;

    /**
     * 子节点列表
     */
    private List<TreeNode> children = new ArrayList<>();

}
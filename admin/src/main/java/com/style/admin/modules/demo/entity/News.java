package com.style.admin.modules.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.style.common.entity.BaseEntity;
import com.style.common.validator.group.AddGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_news")
public class News extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    @NotBlank(message = "{news.title.require}", groups = {AddGroup.class})
    private String title;
    /**
     * 内容
     */
    @NotBlank(message = "{news.content.require}", groups = {AddGroup.class})
    private String content;
    /**
     * 发布时间
     */
    @NotNull(message = "{news.pubDate.require}", groups = {AddGroup.class})
    private Date pubDate;
    /**
     * 创建者dept_id
     */
    private Long deptId;

}

package com.style.login.modules.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.style.web.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("tb_news")
public class News extends BaseEntity<News> {

    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;

}

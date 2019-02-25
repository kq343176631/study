package com.style.admin.modules.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.demo.dao.NewsDao;
import com.style.admin.modules.demo.entity.News;
import com.style.admin.modules.demo.service.NewsService;
import com.style.common.service.impl.CrudServiceImpl;
import com.style.utils.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NewsServiceImpl extends CrudServiceImpl<NewsDao,News> implements NewsService {

    @Override
    protected QueryWrapper<News> getWrapper(Map<String, Object> params) {

        String title =  (String)params.get("title");
        String startDate =  (String)params.get("startDate");
        String endDate =  (String)params.get("endDate");

        QueryWrapper<News> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(title)) {
            wrapper.like("title", "%" + title + "%");
        }
        if (StringUtils.isNotBlank(startDate)) {
            wrapper.ge("pub_date", startDate);

        }
        if (StringUtils.isNotBlank(endDate)) {
            wrapper.le("pub_date", endDate);

        }
        return wrapper;
    }

}

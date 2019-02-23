package com.style.login.modules.demo.service.impl;

import com.style.common.crud.service.impl.BaseServiceImpl;
import com.style.login.modules.demo.dao.NewsDao;
import com.style.login.modules.demo.entity.News;
import com.style.login.modules.demo.service.NewsService;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl extends BaseServiceImpl<NewsDao,News> implements NewsService {

}

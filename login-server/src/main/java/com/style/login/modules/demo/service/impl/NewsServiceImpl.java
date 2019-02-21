package com.style.login.modules.demo.service.impl;

import com.style.login.modules.demo.dao.NewsDao;
import com.style.login.modules.demo.entity.News;
import com.style.login.modules.demo.service.NewsService;
import com.style.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl extends BaseServiceImpl<NewsDao,News> implements NewsService {

}

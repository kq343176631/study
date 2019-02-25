package com.style.test;

import com.style.admin.AdminApplication;
import com.style.admin.modules.demo.entity.News;
import com.style.admin.modules.demo.service.NewsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = AdminApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CrudTest {

    @Autowired
    private NewsService newsService;

    @Test
    public void get(){

        News news = newsService.get(1100035744816193538L);

        System.out.println(news);

    }


    @Test
    public void list(){

    }

    @Test
    public void save(){

    }

}

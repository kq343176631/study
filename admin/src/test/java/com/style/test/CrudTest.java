package com.style.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.AdminApplication;
import com.style.admin.modules.demo.entity.News;
import com.style.admin.modules.demo.service.NewsService;
import com.style.common.model.Page;
import com.style.utils.collect.ListUtils;
import com.style.utils.collect.MapUtils;
import com.style.utils.id.IdGenUtils;
import com.style.utils.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = AdminApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CrudTest {

    @Autowired
    private NewsService newsService;

    @Test
    public void save(){
        News news = new News();
        news.setContent("rqtqt");
        news.setDeptId(280167106L);
        news.setPubDate(new Date());
        news.setTitle("fgggg");
        news.setCreator(1415L);
        news.setUpdater(25126L);
        System.out.println( newsService.save(news));
    }

    @Test
    public void saveBatch(){
        List<News> newsList = ListUtils.newArrayList();
        for(int i=0;i<10;i++){
            News news = new News();
            news.setContent(StringUtils.getRandomStr(10));
            news.setDeptId(IdGenUtils.idWorker());
            news.setPubDate(new Date());
            news.setTitle(StringUtils.getRandomStr(5));
            news.setCreator(IdGenUtils.idWorker());
            news.setUpdater(IdGenUtils.idWorker());
            newsList.add(news);
        }
        if(newsService.saveBatch(newsList,10)){
            System.out.println(true);
        }

    }

    @Test
    public void updateById(){
        News news = new News();
        news.setId(1100250229434814465L);
        news.setContent("333");
        news.setDeptId(333L);
        news.setPubDate(new Date());
        news.setTitle("333");
        news.setCreator(333L);
        news.setUpdater(333L);
        newsService.updateById(news);
    }

    @Test
    public void updateBatch(){

        News news1 = new News();
        news1.setId(1100247981107507201L);
        news1.setContent("111");
        news1.setTitle("111");
        news1.setUpdater(111L);

        News news2 = new News();
        news2.setId(1100247981115895809L);
        news2.setContent("222");
        news2.setTitle("222");
        news2.setUpdater(222L);


        List<News> newsList = ListUtils.newArrayList();

        newsList.add(news1);
        newsList.add(news2);

        newsService.updateBatchById(newsList,10);

    }

    @Test
    public void update(){
        News news1 = new News();
        news1.setContent("77777");
        news1.setTitle("7777");
        news1.setUpdater(7777L);

        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.eq("title","222").or().eq("content","111");
        newsService.update(news1,wrapper);
    }

    @Test
    public void get(){

        News news = newsService.get(1100250229434814465L);

        System.out.println(news);

    }

    @Test
    public void page(){

        Map<String,Object> params = MapUtils.newHashMap();

        params.put("pageNo","1");
        params.put("pageSize","10");
        //params.put("title","777");

        Page<News>  page = newsService.page( params);

        for(News news:page.getRecords()){
            System.out.println(news);
        }
    }

    @Test
    public void list(){

        Map<String,Object> params = MapUtils.newHashMap();

        //params.put("title","777");

        List<News>  newsList = newsService.list( params);

        for(News news:newsList){
            System.out.println(news);
        }

    }

    @Test
    public void deleteById(){

       // newsService.deleteById(1100250229434814465L);
    }

    @Test
    public void deleteByIds(){

        List<Long> ids = ListUtils.newArrayList();
        ids.add(1100250229720027138L);
        ids.add(1100250229728415746L);
        ids.add(1100250229736804353L);
        newsService.deleteByIds(ids);


    }

    @Test
    public void delete(){
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.eq("title","2XQG7").or().eq("content","H56PQFPMXR");
        newsService.delete(wrapper);

    }

}

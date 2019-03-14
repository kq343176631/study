package com.style.test;

import com.style.admin.AdminApplication;
import com.style.cache.CacheUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = AdminApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class RedisTest {

    @Test
    public void put(){

        CacheUtils.put("myName","kongweiqiang");
        String name = CacheUtils.get("myName").toString();
        System.out.println(name);
    }
}

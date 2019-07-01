package com.style.test;

import com.style.study.StudyApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = StudyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class SimpleCaseTest {

    @Test
    public void url() throws Exception {
        URL url = new URL(" http://www.example.com:1080/docs/resource1.html");
        System.out.println("getFile():" + url.getFile());
        System.out.println("getPath():" + url.getPath());
        System.out.println(url.toString());
        URI uri = url.toURI();
        System.out.println(uri.getPath());
    }

    @Test
    public void number() {
        System.out.println((long) 4.8);
    }
}

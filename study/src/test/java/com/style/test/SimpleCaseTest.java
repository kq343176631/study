package com.style.test;

import com.style.study.StudyApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = StudyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class SimpleCaseTest {

    public static void main(String[] args) {
        String str = "";
        str.length();
        String[] strs = {"",""};
    }
}

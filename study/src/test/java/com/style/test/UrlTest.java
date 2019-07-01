package com.style.test;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class UrlTest {

    @Test
    public void test() throws Exception {
        URL url= new URL(" http://www.example.com:1080/docs/resource1.html");
        System.out.println("getFile():"+url.getFile());
        System.out.println("getPath():"+url.getPath());
        System.out.println(url.toString());
        URI uri = url.toURI();
        System.out.println(uri.getPath());
    }

    @Test
    public void number(){
        System.out.println((long)4.8);
    }
}

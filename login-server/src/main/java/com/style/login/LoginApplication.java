package com.style.login;

import com.style.common.io.PropertyUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@EnableEurekaClient
public class LoginApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LoginApplication.class);
        // 自定义属性文件
        //application.setDefaultProperties(PropertyUtils.getInstance().getProperties());
        application.run(args);
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 关闭错误页面过滤器
        this.setRegisterErrorPageFilter(false);
        builder.properties(PropertyUtils.getInstance().getProperties());
        builder.sources(LoginApplication.class);
        return builder;
    }

}

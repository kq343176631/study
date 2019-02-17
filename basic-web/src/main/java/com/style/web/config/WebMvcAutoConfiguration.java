package com.style.web.config;

import com.style.common.GlobalUtils;
import com.style.common.constant.Constants;
import com.style.common.lang.ListUtils;
import com.style.common.lang.StringUtils;
import com.style.web.interceptor.LogInterceptor;
import com.style.web.interceptor.MobileInterceptor;
import com.style.web.mapper.JsonMapper;
import com.style.web.mapper.XmlMapper;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.MultipartConfigElement;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * WebMvcConfig
 */
@Configuration
@AutoConfigureBefore({ValidationAutoConfiguration.class, MultipartAutoConfiguration.class})
@EnableWebMvc
@EnableConfigurationProperties({MultipartProperties.class})
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    private final MultipartProperties multipartProperties;

    public WebMvcAutoConfiguration(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    /**
     * 验证器
     */
    @Bean("beanValidator")
    public LocalValidatorFactoryBean beanValidator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setProviderClass(HibernateValidator.class);
        return factoryBean;
    }

    /**
     * 文件上传
     */
    @Bean("multipartConfigElement")
    @ConditionalOnMissingBean(name = {"multipartConfigElement"})
    public MultipartConfigElement multipartConfigElement() {
        multipartProperties.setMaxFileSize("1024");
        multipartProperties.setMaxRequestSize("1024");
        return this.multipartProperties.createMultipartConfig();
    }

    /**
     * 通用的组模版
     */
    /*@Bean(name = "beetlConfig", initMethod = "init")
    public BeetlGroupUtilConfiguration beetlGroupUtilConfiguration() {
        BeetlGroupUtilConfiguration configuration = new BeetlGroupUtilConfiguration();
        //获取类加载器
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ResourceUtils.getClassLoader();
        }
        ClasspathResourceLoader cprLoader = new ClasspathResourceLoader(loader,
                GlobalUtils.getProperty("web.view.root"));
        configuration.setResourceLoader(cprLoader);
        configuration.setConfigFileResource(ResourceUtils.getResource("classpath:configs/view/beetl.properties"));
        // 加载额外配置文件
        configuration.init();
        //如果使用了优化编译器，涉及到字节码操作，需要添加类加载器。
        configuration.getGroupTemplate().setClassLoader(loader);
        return configuration;
    }*/

    /**
     * 自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 日子拦截器
        InterceptorRegistration logRegistration = registry.addInterceptor(new LogInterceptor());
        String logPathPatterns = GlobalUtils.getProperty("web.interceptor.log.addPathPatterns");
        String logExcludePathPatterns = GlobalUtils.getProperty("web.interceptor.log.excludePathPatterns");
        // 设置需要拦截的路径：URL
        for (String uri : StringUtils.split(logPathPatterns, Constants.SPLIT)) {
            if (StringUtils.isNotBlank(uri)) {
                logRegistration.addPathPatterns(StringUtils.trim(uri));
            }
        }
        // 设置不需要拦截的路径：URL
        for (String uri : StringUtils.split(logExcludePathPatterns, Constants.SPLIT)) {
            if (StringUtils.isNotBlank(uri)) {
                logRegistration.excludePathPatterns(StringUtils.trim(uri));
            }
        }

        // 手机视图拦截器
        InterceptorRegistration mobileRegistration = registry.addInterceptor(new MobileInterceptor());
        String mobilePathPatterns = GlobalUtils.getProperty("web.interceptor.mobile.addPathPatterns");
        String mobileExcludePathPatterns = GlobalUtils.getProperty("web.interceptor.mobile.excludePathPatterns");
        // 设置需要拦截的路径：URL
        for (String uri : StringUtils.split(mobilePathPatterns, Constants.SPLIT)) {
            if (StringUtils.isNotBlank(uri)) {
                mobileRegistration.addPathPatterns(StringUtils.trim(uri));
            }
        }
        // 设置不需要拦截的路径：URL
        for (String uri : StringUtils.split(mobileExcludePathPatterns, Constants.SPLIT)) {
            if (StringUtils.isNotBlank(uri)) {
                mobileRegistration.excludePathPatterns(StringUtils.trim(uri));
            }
        }
    }

    /**
     * 视图解析器
     */
    /*@Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        BeetlViewResolver viewResolver = new BeetlViewResolver();
        viewResolver.setContentType("text/html;charset=UTF-8");
        viewResolver.setOrder(1000);
        viewResolver.setSuffix(".html");
        viewResolver.setConfig(this.beetlGroupUtilConfiguration());
        registry.viewResolver(viewResolver);
        registry.enableContentNegotiation();
    }*/

    /**
     * 开启跨域支持
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/**");
        corsRegistration.allowedOrigins("*");
        corsRegistration.allowCredentials(true);
        corsRegistration.allowedMethods("GET", "POST", "DELETE", "PUT");
        corsRegistration.maxAge(3600 * 24);
    }

    /**
     * 处理静态资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 当请求localhost/resource/1.png时，返回/static/1.png。
        registry.addResourceHandler("/error/**")
                .addResourceLocations("classpath:views/error/");
        /*registry.addResourceHandler("/admin/**")
                .addResourceLocations("classpath:admin/")
                //.setCachePeriod(31536000);
                .setCachePeriod(0);// 开发模式禁用缓存*/
    }

    /**
     * 内容协商机制
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        //是否使用url上的参数来指定数据返回类型
        configurer.favorParameter(false);
        //是否忽略HttpHeader上的Accept指定
        configurer.ignoreAcceptHeader(true);
        //是否通过Url的后缀来指定返回值类型
        configurer.favorPathExtension(true);
        //设置一个默认的返回内容形式，当未明确指定返回内容形式时，使用此设置
        //configurer.defaultContentType(MediaType.APPLICATION_JSON);
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
        configurer.mediaType("xml", MediaType.APPLICATION_XML);
        //configurer.useRegisteredExtensionsOnly(true);

    }

    /**
     * 消息转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // TEXT 消息转换器 将StringHttpMessageConverter的默认编码设为UTF-8
        StringHttpMessageConverter httpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.add(httpMessageConverter);

        // JSON 消息转换器 将Jackson2HttpMessageConverter的默认格式化输出为false
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> jsonMediaTypes = ListUtils.newArrayList();
        jsonMediaTypes.add(MediaType.APPLICATION_JSON);
        jsonHttpMessageConverter.setSupportedMediaTypes(jsonMediaTypes);
        jsonHttpMessageConverter.setPrettyPrint(false);
        jsonHttpMessageConverter.setObjectMapper(JsonMapper.getInstance());
        converters.add(jsonHttpMessageConverter);

        // XML 消息转换器 使用XML格式输出数据
        MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter = new MappingJackson2XmlHttpMessageConverter();
        List<MediaType> xmlMediaTypes = ListUtils.newArrayList();
        xmlMediaTypes.add(MediaType.APPLICATION_XML);
        xmlHttpMessageConverter.setSupportedMediaTypes(xmlMediaTypes);
        xmlHttpMessageConverter.setPrettyPrint(false);
        xmlHttpMessageConverter.setObjectMapper(XmlMapper.getInstance());
        converters.add(xmlHttpMessageConverter);
    }

    /**
     * 处理静态资源
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // 对应浏览器的每次请求
        // 先经过DefaultServletHttpRequestHandler判断是否是静态文件
        // 如果是静态文件，则进行处理
        // 不是，则放行交由DispatcherServlet控制器处理。
        configurer.enable();
    }

    @Override
    public Validator getValidator() {
        return this.beanValidator();
    }
}

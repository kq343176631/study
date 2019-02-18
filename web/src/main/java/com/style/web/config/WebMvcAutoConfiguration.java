package com.style.web.config;

import com.style.common.GlobalUtils;
import com.style.common.constant.Constants;
import com.style.common.lang.ListUtils;
import com.style.common.lang.StringUtils;
import com.style.web.interceptor.LogInterceptor;
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
        multipartProperties.setMaxFileSize(GlobalUtils.getProperty("spring.servlet.multipart.max-file-size"));
        multipartProperties.setMaxRequestSize(GlobalUtils.getProperty("spring.servlet.multipart.max-request-size"));
        return this.multipartProperties.createMultipartConfig();
    }

    /**
     * 自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        if (!Constants.TRUE.equals(GlobalUtils.getProperty("web.interceptor.log.enabled"))) {
            return;
        }
        // 日子拦截器
        InterceptorRegistration logRegistration = registry.addInterceptor(new LogInterceptor());
        String logPathPatterns = GlobalUtils.getProperty("web.interceptor.log.add-path-patterns");
        String logExcludePathPatterns = GlobalUtils.getProperty("web.interceptor.log.exclude-path-patterns");
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

    }

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
        registry.addResourceHandler("/error/**")
                .addResourceLocations("classpath:view/error/");
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

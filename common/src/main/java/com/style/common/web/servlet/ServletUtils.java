package com.style.common.web.servlet;

import com.style.utils.core.GlobalUtils;
import com.style.utils.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Servlet 工具类.
 */
public class ServletUtils {

    // 定义静态文件后缀；
    private static String[] staticFiles;

    // 静态文件排除URI地址
    private static String[] staticFileExcludeUri;

    static {
        staticFiles = StringUtils.split(GlobalUtils.getProperty("web.staticFile"), ",");
        staticFileExcludeUri = StringUtils.split(GlobalUtils.getProperty("web.staticFileExcludeUri"),
                ",");
    }

    /**
     * 获取当前请求对象，需要:org.springframework.web.filter.RequestContextFilter
     */
    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取当前响应对象，需要:org.springframework.web.filter.RequestContextFilter
     */
    public static HttpServletResponse getHttpServletResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }

        return ((ServletRequestAttributes) requestAttributes).getResponse();
    }

    public static Map<String, String[]> getParameterMap(HttpServletRequest request) {

        return request.getParameterMap();
    }

    /**
     * 获取域名
     */
    public static String getDomainName() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        StringBuffer url = request.getRequestURL();
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
    }

    public static String getOrigin() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(HttpHeaders.ORIGIN);
    }

    public static String getLanguage() {
        //默认语言
        String defaultLanguage = "zh-CN";
        //request
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return defaultLanguage;
        }

        //请求语言
        defaultLanguage = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);

        return defaultLanguage;
    }

    public static boolean isStaticFile(String uri) {

        if (staticFileExcludeUri != null) {
            for (String s : staticFileExcludeUri) {
                if (StringUtils.contains(uri, s)) {
                    return false;
                }
            }
        }
        return StringUtils.endsWithAny(uri, staticFiles);
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }
        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
            return true;
        }
        String uri = request.getRequestURI();
        if (StringUtils.inStringIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }
        String ajax = request.getParameter("__ajax");
        return StringUtils.inStringIgnoreCase(ajax, "json", "xml");
    }

    public static void renderString(HttpServletResponse response, String aTrue, String property) {
    }

    public static Map<String, Object> getExtParams(ServletRequest request) {

        return null;
    }
}


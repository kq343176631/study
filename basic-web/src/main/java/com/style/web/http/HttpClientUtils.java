package com.style.web.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP客户端工具类（支持HTTPS）
 */
public class HttpClientUtils {
    /**
     * http的get请求
     */
    public static String get(String url) {
        return get(url, "UTF-8");
    }

    /**
     * http的get请求
     *
     * @param url url
     */
    public static String get(String url, String charset) {
        return executeRequest(new HttpGet(url), charset);
    }

    /**
     * http的get请求，增加异步请求头参数
     */
    public static String ajaxGet(String url) {
        return ajaxGet(url, "UTF-8");
    }

    /**
     * http的get请求，增加异步请求头参数
     */
    public static String ajaxGet(String url, String charset) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
        return executeRequest(httpGet, charset);
    }

    /**
     * http的post请求，传递map格式参数
     */
    public static String post(String url, Map<String, String> dataMap, String charset) {
        HttpPost httpPost = new HttpPost(url);
        insertDataMap(httpPost, dataMap, charset);
        return executeRequest(httpPost, charset);
    }

    /**
     * http的post请求，增加异步请求头参数，传递map格式参数
     */
    public static String ajaxPost(String url, Map<String, String> dataMap) {
        return ajaxPost(url, dataMap, "UTF-8");
    }

    /**
     * http的post请求，增加异步请求头参数，传递map格式参数
     */
    public static String ajaxPost(String url, Map<String, String> dataMap, String charset) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        insertDataMap(httpPost, dataMap, charset);
        return executeRequest(httpPost, charset);
    }

    /**
     * http的post请求，增加异步请求头参数，传递json格式参数
     */
    public static String ajaxPostJson(String url, String jsonString) {
        return ajaxPostJson(url, jsonString, "UTF-8");
    }

    /**
     * http的post请求，增加异步请求头参数，传递json格式参数
     */
    public static String ajaxPostJson(String url, String jsonString, String charset) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        // 解决中文乱码问题
        StringEntity stringEntity = new StringEntity(jsonString, charset);
        stringEntity.setContentEncoding(charset);
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        return executeRequest(httpPost, charset);
    }

    /**
     * 执行一个http请求，传递HttpGet或HttpPost参数
     */
    public static String executeRequest(HttpUriRequest httpRequest) {
        return executeRequest(httpRequest, "UTF-8");
    }

    /**
     * 执行一个http请求，传递HttpGet或HttpPost参数
     */
    public static String executeRequest(HttpUriRequest httpRequest, String charset) {
        CloseableHttpClient httpclient;
        if ("https".equals(httpRequest.getURI().getScheme())) {
            httpclient = createSSLInsecureClient();
        } else {
            httpclient = HttpClients.createDefault();
        }
        String result;
        try {
            HttpEntity entity = null;
            try (CloseableHttpResponse response = httpclient.execute(httpRequest)) {
                entity = response.getEntity();
                result = EntityUtils.toString(entity, charset);
            } catch (IOException ex) {
                return null;
            } finally {
                EntityUtils.consume(entity);
                httpclient.close();
            }
        } catch (IOException ex) {
            return null;
        }
        return result;
    }

    /**
     * 创建SSL连接
     */
    public static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 添加数据
     */
    private static void insertDataMap(HttpPost httpPost, Map<String, String> dataMap, String charset) {
        try {
            if (dataMap != null) {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs, charset);
                formEntity.setContentEncoding(charset);
                httpPost.setEntity(formEntity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}


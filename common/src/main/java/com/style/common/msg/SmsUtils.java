package com.style.common.msg;

import com.style.common.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发送短信（请实现send方法）
 */
public class SmsUtils {

    private final static Logger logger = LoggerFactory.getLogger(SmsUtils.class);

    /**
     * 模拟发送短信
     *
     * @param content 短信内容
     * @param mobile  接受手机号码
     */
    public static String send(String content, String mobile) {
        String url = "msg.sms.url";
        String data = "msg.sms.data";
        String prefix = "msg.sms.prefix";
        String suffix = "msg.sms.suffix";
        Connection conn = Jsoup.connect(url);
        conn.postDataCharset("UTF-8");
        conn.method(Method.POST);
        for (String param : StringUtils.split(data, "&")) {
            String[] ss = StringUtils.split(param, "=");
            if (ss.length == 1) {
                conn.data(ss[0], "");
            } else if (ss.length == 2) {
                conn.data(ss[0], ss[1]);
            }
        }
        // 手机号码
        conn.data("mobile", mobile);
        // 短信内容
        conn.data("content", prefix + content + suffix);
        logger.warn("模拟发送短信成功！请实现 " + SmsUtils.class + " 的 send 方法。");
        return "{result:0,message:\"模拟发送短信成功！\"}";
    }

}

package com.style.common;

import com.style.common.lang.ListUtils;
import com.style.common.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class FilterUtils {

    private static final Logger logger = LoggerFactory.getLogger(FilterUtils.class);

    // 预编译SQL过滤正则表达式
    private static Pattern sqlPattern = Pattern.compile("(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)", Pattern.CASE_INSENSITIVE);


    // 预编译XSS过滤正则表达式
    private static List<Pattern> xssPatterns = ListUtils.newArrayList(
            Pattern.compile("(<\\s*(script|link|style|iframe)([\\s\\S]*?)(>|<\\/\\s*\\1\\s*>))|(</\\s*(script|link|style|iframe)\\s*>)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*(href|src)\\s*=\\s*(\"\\s*(javascript|vbscript):[^\"]+\"|'\\s*(javascript|vbscript):[^']+'|(javascript|vbscript):[^\\s]+)\\s*(?=>)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*on[a-z]+\\s*=\\s*(\"[^\"]+\"|'[^']+'|[^\\s]+)\\s*(?=>)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(eval\\((.*?)\\)|xpression\\((.*?)\\))", Pattern.CASE_INSENSITIVE)
    );

    /**
     * 执行 SQL 过滤
     */
    public static String doSqlFilter(String input) {

        if (StringUtils.isBlank(input)) {
            return null;
        }
        //去掉'|"|;|\字符
        input = StringUtils.replace(input, "'", "");
        input = StringUtils.replace(input, "\"", "");
        input = StringUtils.replace(input, ";", "");
        input = StringUtils.replace(input, "\\", "");
        //转换成小写
        input = input.toLowerCase();
        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};
        //判断是否包含非法字符
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                input.replaceAll(keyword, "");
            }
        }
        return input;
    }

    /**
     * 执行 SQL 过滤（使用正则表达式）
     *
     */
    public static String doSqlFilterWithRegx(String text) {
        if (text != null) {
            String value = text;
            Matcher matcher = sqlPattern.matcher(text);
            if (matcher.find()) {
                value = matcher.replaceAll(StringUtils.EMPTY);
            }
            if (logger.isWarnEnabled() && !value.equals(text)) {
                logger.info("sqlFilter: {}   <=<=<=   {}", value, text);
                return StringUtils.EMPTY;
            }
            return value;
        }
        return null;
    }

    /**
     * 执行 XSS 过滤
     */
    public static String doXssFilter(String html) {
        return Jsoup.clean(html, getXssWhitelist());
    }

    /**
     * 执行 XSS 过滤（使用正则表达式）
     */
    public static String doXssFilterWithRegx(String text) {

        String oriValue = StringUtils.trim(text);
        if (text != null) {
            String value = oriValue;
            for (Pattern pattern : xssPatterns) {
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    value = matcher.replaceAll(StringUtils.EMPTY);
                }
            }
            // 如果开始不是HTML，XML，JOSN格式，则再进行HTML的 "、<、> 转码。
            if (!StringUtils.startsWithIgnoreCase(value, "<!--HTML-->")    // HTML
                    && !StringUtils.startsWithIgnoreCase(value, "<?xml ")    // XML
                    && !StringUtils.contains(value, "id=\"FormHtml\"")        // JFlow
                    && !(StringUtils.startsWith(value, "{") && StringUtils.endsWith(value, "}")) // JSON Object
                    && !(StringUtils.startsWith(value, "[") && StringUtils.endsWith(value, "]")) // JSON Array
                    ) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < value.length(); i++) {
                    char c = value.charAt(i);
                    switch (c) {
                        case '>':
                            sb.append("＞");
                            break;
                        case '<':
                            sb.append("＜");
                            break;
                        case '\'':
                            sb.append("＇");
                            break;
                        case '\"':
                            sb.append("＂");
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                }
                value = sb.toString();
            }
            if (logger.isInfoEnabled() && !value.equals(oriValue)) {
                logger.info("xssFilter: {}   <=<=<=   {}", value, text);
            }
            return value;
        }
        return null;
    }

    /**
     * XSS过滤白名单
     */
    private static Whitelist getXssWhitelist() {
        return new Whitelist()
                //支持的标签
                .addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "dl",
                        "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small",
                        "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul",
                        "embed", "object", "param", "span")

                //支持的标签属性
                .addAttributes("a", "href", "class", "style", "target", "rel", "nofollow")
                .addAttributes("blockquote", "cite")
                .addAttributes("code", "class", "style")
                .addAttributes("col", "span", "width")
                .addAttributes("colgroup", "span", "width")
                .addAttributes("img", "align", "alt", "height", "src", "title", "width", "class", "style")
                .addAttributes("ol", "start", "type")
                .addAttributes("q", "cite")
                .addAttributes("table", "summary", "width", "class", "style")
                .addAttributes("tr", "abbr", "axis", "colspan", "rowspan", "width", "style")
                .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width", "style")
                .addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width", "style")
                .addAttributes("ul", "type", "style")
                .addAttributes("pre", "class", "style")
                .addAttributes("div", "class", "id", "style")
                .addAttributes("embed", "src", "wmode", "flashvars", "pluginspage", "allowFullScreen", "allowfullscreen",
                        "quality", "width", "height", "align", "allowScriptAccess", "allowscriptaccess", "allownetworking", "type")
                .addAttributes("object", "type", "id", "name", "data", "width", "height", "style", "classid", "codebase")
                .addAttributes("param", "name", "value")
                .addAttributes("span", "class", "style")

                //标签属性对应的协议
                .addProtocols("a", "href", "ftp", "http", "https", "mailto")
                .addProtocols("img", "src", "http", "https")
                .addProtocols("blockquote", "cite", "http", "https")
                .addProtocols("cite", "cite", "http", "https")
                .addProtocols("q", "cite", "http", "https")
                .addProtocols("embed", "src", "http", "https");
    }
}

package com.style.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.style.utils.filter.FilterUtils;
import com.style.utils.lang.ListUtils;
import com.style.utils.lang.MapUtils;
import com.style.utils.lang.StringUtils;
import com.style.common.web.cookie.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 分页对象
 *
 * @param <T>
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public static final int COUNT_ONLY_COUNT = -2;
    @JsonIgnore
    public static final int COUNT_NOT_COUNT = -1;
    @JsonIgnore
    public static final int PAGE_SIZE_NOT_PAGING = -1;

    // 当前页码
    private int pageNo;
    // 分页大小
    private int pageSize;
    // 总记录数，设置为“-1”表示不查询总数
    private long count;

    private int first;// 首页索引
    private int last;// 尾页索引
    private int prev;// 上一页索引
    private int next;// 下一页索引
    // 是否为第一页
    private boolean firstPageFlag;
    // 是否为最后一页
    private boolean lastPageFlag;

    // 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。
    private String funcName = "page";
    // 函数的附加参数，第三个参数值。
    private String funcParam = "";

    // 排序字段（参数）
    @JsonIgnore
    private String orderBy;
    // 页面显示的页码个数 中间值
    private int length = 9;
    // 页面省略的页面个数 总长度
    private int bothNum = 3;

    // 分页信息
    private String pageInfo;
    // 分页数据
    private List<T> list = ListUtils.newArrayList();
    // 其它数据
    private Map<String, Object> otherData;

    /**
     * 默认构造器
     */
    public Page() {

    }

    /**
     * 分页对象构造器
     *
     * @param pageNo   pageNo
     * @param pageSize pageSize
     */
    public Page(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    /**
     * 分页对象构造器
     *
     * @param request  request
     * @param response response
     */
    public Page(HttpServletRequest request, HttpServletResponse response) {
        // 设置页码参数（传递rePage参数，来记住页码）
        String pageNo;
        if (StringUtils.isNumeric(pageNo = request.getParameter("pageNo"))) {
            CookieUtils.setCookie(response, "pageNo", pageNo);
            this.setPageNo(Integer.parseInt(pageNo));
        } else {
            if (request.getParameter("rePage") != null && StringUtils.isNumeric(pageNo = CookieUtils.getCookie(request, "pageNo"))) {
                this.setPageNo(Integer.parseInt(pageNo));
            }
        }
        // 设置分页大小
        String pageSize;
        if (StringUtils.isNumeric(pageSize = request.getParameter("pageSize"))) {
            CookieUtils.setCookie(response, "pageSize", pageSize);
            this.setPageSize(Integer.parseInt(pageSize));
        } else {
            if (request.getParameter("rePage") != null && StringUtils.isNumeric(pageSize = CookieUtils.getCookie(request, "pageSize"))) {
                this.setPageSize(Integer.parseInt(pageSize));
            }
        }
        // 设置排序参数
        String orderBy;
        if (StringUtils.isNotBlank(orderBy = request.getParameter("orderBy"))) {
            this.setOrderBy(orderBy);
        }
    }

    public void initialize() {
        if (this.pageNo < 1) {
            // 使用默认值
            this.pageNo = 1;
        }
        if (this.pageSize < 1) {
            // 使用默认值
            this.pageSize = 10;
        }
        this.first = 1;
        // 计算尾页
        this.last = (int) (this.count / (long) (this.pageSize) + (long) this.first - 1L);
        if (this.count % (long) this.pageSize != 0L || this.last == 0) {
            ++this.last;
        }
        if (this.last < this.first) {
            this.last = this.first;
        }
        // 判断当前页是否为第一页
        if (this.pageNo <= 1) {
            this.pageNo = this.first;
            this.firstPageFlag = true;
        } else {
            this.firstPageFlag = false;
        }
        // 判断当前页是否为最后一页
        if (this.pageNo >= this.last) {
            this.pageNo = this.last;
            this.lastPageFlag = true;
        } else {
            this.lastPageFlag = false;
        }
        // 计算下一页
        if (this.pageNo < this.last - 1) {
            this.next = this.pageNo + 1;
        } else {
            this.next = this.last;
        }
        // 计算前一页
        if (this.pageNo > 1) {
            this.prev = this.pageNo - 1;
        } else {
            this.prev = this.first;
        }
        // 如果当前页小于首页
        if (this.pageNo < this.first) {
            this.pageNo = this.first;
        }
        // 如果当前页大于尾页
        if (this.pageNo > this.last) {
            this.pageNo = this.last;
        }
    }

    /**
     * 默认输出当前分页标签
     * <div class="page">${page}</div>
     */
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        if (this.funcName == null) {
            this.funcName = "page";
        }

        if (this.funcParam == null) {
            this.funcParam = "";
        }

        int begin;
        if ((begin = this.pageNo - this.length / 2) < this.first) {
            begin = this.first;
        }

        int end;
        if ((end = begin + this.length - 1) >= this.last) {
            end = this.last;
            if ((begin = this.last - this.length + 1) < this.first) {
                begin = this.first;
            }
        }

        // 如果是首页
        if (this.pageNo == this.first) {
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">&#171; 上一页</a></li>\n");
        } else {
            sb.append("<li><a href=\"javascript:\" onclick=\"")
                    .append(funcName)
                    .append("(")
                    .append(prev)
                    .append(",")
                    .append(pageSize)
                    .append(",'")
                    .append(funcParam)
                    .append("');\">&#171; 上一页</a></li>\n");
        }
        // 开始页大于首页
        if (begin > this.first) {
            int i;
            for (i = first; i < first + this.bothNum && i < begin; i++) {
                sb.append("<li><a href=\"javascript:\" onclick=\"")
                        .append(funcName)
                        .append("(")
                        .append(i)
                        .append(",")
                        .append(pageSize)
                        .append(",'")
                        .append(funcParam)
                        .append("');\">")
                        .append((i + 1 - first))
                        .append("</a></li>\n");
            }
            if (i < begin) {
                sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>\n");
            }
        }

        for (int i = begin; i <= end; i++) {
            if (i == pageNo) {
                sb.append("<li class=\"active\"><a href=\"javascript:\">")
                        .append((i + 1 - first))
                        .append("</a></li>\n");
            } else {
                sb.append("<li><a href=\"javascript:\" onclick=\"")
                        .append(funcName)
                        .append("(")
                        .append(i)
                        .append(",")
                        .append(pageSize)
                        .append(",'")
                        .append(funcParam)
                        .append("');\">")
                        .append((i + 1 - first))
                        .append("</a></li>\n");
            }
        }

        if (last - end > this.bothNum) {
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>\n");
            end = last - this.bothNum;
        }

        for (int i = end + 1; i <= last; i++) {
            sb.append("<li><a href=\"javascript:\" onclick=\"")
                    .append(funcName)
                    .append("(")
                    .append(i)
                    .append(",")
                    .append(pageSize)
                    .append(",'")
                    .append(funcParam)
                    .append("');\">")
                    .append((i + 1 - first))
                    .append("</a></li>\n");
        }

        if (pageNo == last) {
            sb.append("<li class=\"disabled\"><a href=\"javascript:\">下一页 &#187;</a></li>\n");
        } else {
            sb.append("<li><a href=\"javascript:\" onclick=\"")
                    .append(funcName)
                    .append("(")
                    .append(next)
                    .append(",")
                    .append(pageSize)
                    .append(",'")
                    .append(funcParam)
                    .append("');\">")
                    .append("下一页 &#187;</a></li>\n");
        }

        sb.append("<li class=\"disabled controls\"><a href=\"javascript:\">当前 ");
        sb.append("<input type=\"text\" value=\"")
                .append(pageNo)
                .append("\" onkeypress=\"var e=window.event||event;var c=e.keyCode||e.which;if(c==13)");
        sb.append(funcName)
                .append("(this.value,")
                .append(pageSize)
                .append(",'")
                .append(funcParam)
                .append("');\" onclick=\"this.select();\"/> / ");
        sb.append("<input type=\"text\" value=\"")
                .append(pageSize)
                .append("\" onkeypress=\"var e=window.event||event;var c=e.keyCode||e.which;if(c==13)");
        sb.append(funcName)
                .append("(")
                .append(pageNo)
                .append(",this.value,'")
                .append(funcParam)
                .append("');\" onclick=\"this.select();\"/> 条，");
        sb.append("共 ")
                .append(count)
                .append(" 条")
                .append((this.pageInfo != null ? this.pageInfo : ""))
                .append("</a></li>\n");
        sb.insert(0, "<ul>\n")
                .append("</ul>\n");
        sb.append("<div style=\"clear:both;\"></div>");
        return sb.toString();
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public long getCount() {
        return this.count;
    }

    public boolean getFirstPageFlag() {
        return this.firstPageFlag;
    }

    public int getNext() {
        return this.lastPageFlag ? this.pageNo : this.pageNo + 1;
    }

    @JsonIgnore
    public int getMaxResults() {
        return this.getPageSize();
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize;
    }

    public int getLast() {
        return this.last;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @JsonIgnore
    public int getFirstResult() {
        int firstResult = (this.getPageNo() - 1) * this.getPageSize();
        if (this.getCount() != -1L && (long) firstResult >= this.getCount()) {
            firstResult = 0;
        }
        return firstResult;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setOtherData(Map<String, Object> otherData) {
        this.otherData = otherData;
    }

    public List<T> getList() {
        return this.list;
    }

    public void addOtherData(String key, Object value) {
        if (this.otherData == null) {
            this.otherData = MapUtils.newHashMap();
        }
        this.otherData.put(key, value);
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Page<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public boolean getLastPageFlag() {
        return this.lastPageFlag;
    }

    public void setBothNum(int bothNum) {
        this.bothNum = bothNum;
    }

    public Map<String, Object> getOtherData() {
        return this.otherData;
    }

    public void setCount(long count) {
        if ((this.count = count) != -1L && (long) this.pageSize >= count) {
            this.pageNo = 1;
        }
        this.initialize();
    }

    @JsonIgnore
    public String getOrderBy() {
        return FilterUtils.doSqlFilter(this.orderBy);
    }

    public int getFirst() {
        return this.first;
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public int getPrev() {
        return this.firstPageFlag ? this.pageNo : this.pageNo - 1;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public void setPageInfo(String pageInfo) {
        this.pageInfo = pageInfo;
    }

    public void setFuncParam(String funcParam) {
        this.funcParam = funcParam;
    }

    public boolean isFirstPageFlag() {
        return firstPageFlag;
    }

    public boolean isLastPageFlag() {
        return lastPageFlag;
    }

    public String getFuncName() {
        return funcName;
    }

    public String getFuncParam() {
        return funcParam;
    }

    public int getLength() {
        return length;
    }

    public int getBothNum() {
        return bothNum;
    }

    public String getPageInfo() {
        return pageInfo;
    }

    public boolean isNotCount() {
        return false;
    }
}

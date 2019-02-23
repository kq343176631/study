package com.style.utils.constant;

/**
 * 系统常量类
 */
public interface Constants {

    //===========================================================//
    //===============         基  本  常  量      ================//
    //===========================================================//

    String POINT = ".";
    String SPLIT = ",";
    String COLON = ":";
    String EMPTY = "";
    String DEFAULT = "default";

    String YES = "yes";
    String NO = "no";

    String TRUE = "true";
    String FALSE = "false";

    String GET = "GET";
    String POST = "POST";

    String GETTER_PREFIX = "get";
    String SETTER_PREFIX = "set";

    /**
     * 当前页码
     */
    String PAGE_NO = "pageNo";
    /**
     * 每页显示记录数
     */
    String PAGE_SIZE = "pageSize";
    /**
     * 排序字段
     */
    String ORDER_BY = "orderBy";
    /**
     * 默认排序字段
     */
    String DEFAULT_ORDER_BY = "updateDate";
    /**
     * 排序方式（asc，desc）
     */
    String ORDER = "order";
    /**
     * 升序
     */
    String ASC = "asc";
    /**
     * 降序
     */
    String DESC = "desc";

    /**
     * 当前页码
     */
    String PAGE = "page";

    /**
     * 每页显示记录数
     */
    String LIMIT = "limit";
    /**
     * 排序字段
     */
    String ORDER_FIELD = "orderField";

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    String DEL_FLAG_NORMAL = "0";
    String DEL_FLAG_DELETE = "1";
    String DEL_FLAG_AUDIT = "2";


    //===========================================================//
    //===============         系  统  缓  存      ================//
    //===========================================================//

    String USER_CACHE = "user_cache";
    String USER_CACHE_ID_ = "user_cache_id_";
    String USER_CACHE_LOGIN_NAME_ = "user_cache_login_name";
    String USER_CACHE_LIST_BY_OFFICE_ID_ = "user_cache_office_id";

    String CACHE_DICT_MAP = "dictMap";


    //===========================================================//
    //===============         系  统  安  全      ================//
    //===========================================================//

    int SALT_SIZE = 8;
    int HASH_INTERATIONS = 1024;
    String HASH_ALGORITHM = "SHA-1";


    //===========================================================//
    //===============         系  统  安  全      ================//
    //===========================================================//

    String DATA_SOURCE_KEY_PREFIX = "target-datasource";
    String DATA_SOURCE_NAME_KEY_PREFIX = "target-datasource.datasource-name";
    String XA_DATA_SOURCE_CLASS_NAME = "com.alibaba.druid.pool.xa.DruidXADataSource";

}

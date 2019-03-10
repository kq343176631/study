package com.style.common.constant;

/**
 * 系统常量类
 */
public interface Constants {

    //===========================================================//
    //===============         基  本  常  量      ================//
    //===========================================================//

    /**
     * 成功
     */
    int SUCCESS = 1;
    /**
     * 失败
     */
    int FAIL = 0;
    /**
     * OK
     */
    String OK = "OK";

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

    /**
     * 升序
     */
    String ASC = "asc";
    /**
     * 降序
     */
    String DESC = "desc";

    //===========================================================//
    //===============         分  页  常  量      ================//
    //===========================================================//

    /**
     * 当前页码
     */
    String PAGE_NO = "pageNo";
    /**
     * 分页大小
     */
    String PAGE_SIZE = "pageSize";

    /**
     * 排序字段
     */
    String ORDER_FIELD = "orderBy";

    /**
     * 默认排序字段
     */
    String DEFAULT_ORDER_FIELD = "updateDate";

    /**
     * 排序方式（asc，desc）
     */
    String ORDER_METHOD = "order";

    /**
     * 云存储配置KEY
     */
    String CLOUD_STORAGE_CONFIG_KEY = "CLOUD_STORAGE_CONFIG_KEY";
    /**
     * 短信配置KEY
     */
    String SMS_CONFIG_KEY = "SMS_CONFIG_KEY";
    /**
     * 邮件配置KEY
     */
    String MAIL_CONFIG_KEY = "MAIL_CONFIG_KEY";


    /**
     * 定时任务状态
     */
    enum ScheduleStatus {
        /**
         * 暂停
         */
        PAUSE(0),
        /**
         * 正常
         */
        NORMAL(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 短信服务商
     */
    enum SmsService {
        /**
         * 阿里云
         */
        ALIYUN(1),
        /**
         * 腾讯云
         */
        QCLOUD(2);

        private int value;

        SmsService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3),
        /**
         * FASTDFS
         */
        FASTDFS(4),
        /**
         * 本地
         */
        LOCAL(5);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

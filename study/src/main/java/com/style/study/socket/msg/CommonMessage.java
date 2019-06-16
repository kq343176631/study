package com.style.study.socket.msg;

import com.style.study.socket.utils.ConvertUtils;
import com.style.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.Arrays;

public class CommonMessage {

    private static final Logger logger = LoggerFactory.getLogger(CommonMessage.class);
    /**
     * 默认开始位置
     */
    private static final int DEFAULT_START_POS = 0;

    /**
     * 默认消息长度：128 byte
     */
    private static final int DEFAULT_MSG_BODY_LEN = 128;

    /**
     * 最大消息长度：32M
     */
    private static final int MAX_MSG_BODY_LEN = 48 * 1024 * 1024;

    /**
     * 消息头：存放命令字。
     */
    private int header;

    /**
     * 当前body中已存放的字节数量。
     */
    private int length;

    /**
     * 消息体
     */
    private byte[] body;

    /**
     * 字节数组的偏移量
     */
    private int pos;

    public CommonMessage() {
        this.header = '\0';
        this.length = 0;
        this.pos = 0;
        this.body = null;
    }

    public CommonMessage(int header, int length, byte[] body) {
        this.header = header;
        if (length <= 0) {
            this.length = 0;
            this.pos = 0;
            this.body = null;
        } else {
            addByteArrayToBody(body, length);
        }
    }

    /**
     * 添加一个String类型的数据到消体
     * @param data 需要添加到消息体中的数据
     * @param byteOrder 字节序
     * @return 是否添加成功
     */
    public boolean addStringToBody(String data, ByteOrder byteOrder){
        if(StringUtils.isBlank(data)){
            logger.error("data is null...");
            return false;
        }
        if(byteOrder==null){
            byteOrder=ByteOrder.BIG_ENDIAN;
        }
        byte[] array = ConvertUtils.stringToByteArray(data);
        if(array==null){
            logger.error("Convert string to byte array failed,string:"+data);
            return false;
        }
        return addByteArrayToBody(array,array.length);
    }

    /**
     * @param byteArray 字节数组
     * @param maxBytes  需要添加到消息体中的最大字节数。
     * @return 是否添加成功
     */
    private boolean addByteArrayToBody(byte[] byteArray, int maxBytes) {
        if (byteArray == null) {
            logger.error("Byte array is null...");
            return false;
        }
        if (maxBytes <= 0) {
            logger.error("Max Bytes less more zero...");
            return false;
        }
        if (this.length + maxBytes > MAX_MSG_BODY_LEN) {
            logger.error("It will more than max of msg body....");
        }
        // 待添加到消息体中的字节数组
        byte[] addedByteArray = Arrays.copyOf(byteArray, maxBytes);
        // 当前消息体的长度
        int curBodyLen = 0;
        if (body != null) {
            curBodyLen = body.length;
        }
        // 判断是否需要扩容
        if (this.body == null || this.length + maxBytes > curBodyLen) {
            // 需要进行扩容
            int newBodyLen = calculateNewBodyLen(maxBytes);
            // 构建新的消息体
            byte[] newBody = new byte[newBodyLen];
            if (this.body != null) {
                // 移植消息体
                System.arraycopy(this.body, DEFAULT_START_POS, newBody, DEFAULT_START_POS, this.length);
            }
            this.body = newBody;
        }
        // 将字节数组拷贝到消息体中
        System.arraycopy(addedByteArray, DEFAULT_START_POS, body, pos, maxBytes);
        pos += maxBytes;
        length += maxBytes;
        return true;
    }

    /**
     * 计算扩容后的消息体的长度。
     *
     * @param needAddLen 需要增加的长度
     * @return 扩容后的长度
     */
    private int calculateNewBodyLen(int needAddLen) {
        int curBodyLen = DEFAULT_MSG_BODY_LEN;
        if (body != null) {
            curBodyLen = (body.length > DEFAULT_MSG_BODY_LEN) ? this.body.length : DEFAULT_MSG_BODY_LEN;
        }
        if (needAddLen <= 0) {
            return curBodyLen;
        }
        int newBodyLen;
        for (int idx = 1; idx < Short.MAX_VALUE; idx++) {
            newBodyLen = idx * curBodyLen;
            if (newBodyLen > MAX_MSG_BODY_LEN) {
                return MAX_MSG_BODY_LEN;
            }
            if (newBodyLen > length + needAddLen) {
                return newBodyLen;
            }
        }
        return MAX_MSG_BODY_LEN;
    }


}

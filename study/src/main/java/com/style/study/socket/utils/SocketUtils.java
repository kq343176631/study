package com.style.study.socket.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SocketUtils {

    private static Logger logger = LoggerFactory.getLogger(SocketUtils.class);

    /**
     * 头部内容长度：用于存放命令字。
     */
    private static final int NET_MSG_HEADER_FIELD_LEN=4;
    /**
     * 头部Length字段长度：用于存放body的长度。
     */
    private static final int NET_MSG_LENGTH_FILED_LEN=4;

    /**
     * 头部字段总长度：NET_MSG_HEADER_FIELD_LEN+NET_MSG_LENGTH_FILED_LEN
     */
    private static final int NET_MES_HEADER_TOTAL_LEN=8;



    /**
     * 默认缓存区大小：1024
     */
    private static final int NET_DEFAULT_BUFFER_SIZE=1024;
    /**
     * ByteBuffer的起始位置
     */
    private static final int NET_BUFFER_START_POS=0;

    public static ByteBuffer readDataFromSocket(Socket socket, InputStream in, ByteOrder order) {

        if(socket==null){
            logger.error("Socket is null...");
            return null;
        }
        if(order==null){
            logger.error("Byte order is null...");
            return null;
        }
        // 获取客户端地址
        SocketAddress remoteAddress = socket.getRemoteSocketAddress();
        // 从输入流中获取请求头
        int headerLen = NET_MSG_HEADER_FIELD_LEN+NET_MSG_LENGTH_FILED_LEN;
        ByteBuffer header = readRawByteBufferFromSocket(socket,in,headerLen);
        if(header==null){
            logger.error("Get header buffer failed from socket input stream,client address:"+remoteAddress);
            return null;
        }
        header.position(NET_BUFFER_START_POS+NET_MSG_HEADER_FIELD_LEN);
        header.order(order);
        // 获取消息体长度
        int bodyLen = header.getInt();


        return null;
    }

    private static ByteBuffer readRawByteBufferFromSocket(Socket socket, InputStream in,int len){
        int bufferSize = len;
        if(len>=NET_DEFAULT_BUFFER_SIZE){
            bufferSize=NET_DEFAULT_BUFFER_SIZE;
        }
        ByteBuffer result = ByteBuffer.allocate(len);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        int readLen;
        int totalLen=0;
        SocketAddress socketAddress = socket.getRemoteSocketAddress();
        try{
            while(totalLen<len){
                readLen = in.read(buffer.array());
                if(readLen<=0){
                    logger.error("Failed to read msg form socket,client address:"+socketAddress);
                    return null;
                }
                // 将buffer中的数据拷贝到result中
                System.arraycopy(buffer.array(),0,result.array(),totalLen,readLen);
                totalLen+=readLen;
                buffer.clear();
            }
        }catch (SocketTimeoutException e){
            logger.error("Read msg from socket occurred timeout,client address:"+socketAddress,e);
            return null;
        } catch (IOException e) {
            logger.error("Read msg from socket occurred io exception,client address:"+socketAddress,e);
            return null;
        }
        //复位result的position
        result.position(NET_BUFFER_START_POS);
        return result;

    }
}

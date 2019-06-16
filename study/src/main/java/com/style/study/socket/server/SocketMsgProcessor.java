package com.style.study.socket.server;

import com.style.study.socket.cmd.CommandHandler;
import com.style.study.socket.msg.CommonMessage;
import com.style.study.socket.msg.ResponseMessage;
import com.style.study.socket.utils.ConvertUtils;
import com.style.study.socket.utils.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 处理Socket消息
 */
public class SocketMsgProcessor implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(SocketMsgProcessor.class);

    private static CommandHandler cmdHandler = new CommandHandler();

    private Socket socket;

    public SocketMsgProcessor(Socket socket){

        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream;
        OutputStream outputStream;
        try{
            socket.setSoLinger(true,0);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            handleSocketMsg(socket,inputStream,outputStream);

        }catch (IOException e){
            //
        }

    }

    private void handleSocketMsg(Socket socket,InputStream in,OutputStream out) throws IOException {
        // 从输入流中获取消息对象
        DataInputStream dis = new DataInputStream(in);
        DataOutputStream dos = new DataOutputStream(out);
        CommonMessage request = getRequestMessage(socket,dis);
        if(request==null){
            logger.error("Get request msg from socket input stream failed...");
            return;
        }
        // 开始处理请求消息
        ResponseMessage response=cmdHandler.handleRequestMessage(request,dis,dos);
        if(response==null){
            logger.error("Command handle return response is null,client address:"+socket.getRemoteSocketAddress());
            return;
        }
        // 将返回的消息转换为字节换成区
        ByteBuffer byteBuffer = ConvertUtils.responseToByteBuffer(response,ByteOrder.BIG_ENDIAN);
        if(byteBuffer==null){
            logger.error("Convert response msg to byte buffer failed,client address:"+socket.getRemoteSocketAddress());
            return;
        }
        // 将byte buffer 写入输出流中。
        dos.write(byteBuffer.array());
        // 刷新缓冲区
        dos.flush();
    }

    private CommonMessage getRequestMessage(Socket socket, InputStream in){

        // 从输出流中获取对应的字节缓冲区
        ByteBuffer byteBuffer = SocketUtils.readDataFromSocket(socket,in,ByteOrder.LITTLE_ENDIAN);
        if(byteBuffer==null){
            logger.error("Get Request msg from socket input stream failed....");
            return null;
        }
        // 将字节缓存区转换为请求消息
        CommonMessage request=ConvertUtils.byteBufferToRequest(byteBuffer,ByteOrder.LITTLE_ENDIAN);
        if(request==null){
            logger.error("Convert byte buffer to request msg failed,client address:"+socket.getRemoteSocketAddress());
            return null;
        }
        return request;
    }

}

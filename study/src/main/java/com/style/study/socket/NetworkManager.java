package com.style.study.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class NetworkManager {

    private static Logger logger = LoggerFactory.getLogger(NetworkManager.class);

    /**
     * socket time out
     */
    private static final int SOCKET_TIMEOUT=1000;

    /**
     *  本类的单例
     */
    private static NetworkManager instance = new NetworkManager();

    /**
     * 服务端SOCKET
     */
    private ServerSocket serverSocket = null;

    private NetworkManager(){

    }

    public static NetworkManager getInstance(){
        return instance;
    }

    /**
     * 初始化SOCKET服务
     * @return true/false
     */
    public boolean initSocketServer(InetSocketAddress address){

        try {
            this.serverSocket = new ServerSocket();
            // 是否运行重复绑定
            this.serverSocket.setReuseAddress(true);
            this.serverSocket.bind(address);
            // 如果在指定时间内，无法从连接队列中取出socket，就会抛出SocketTimeoutException。
            this.serverSocket.setSoTimeout(SOCKET_TIMEOUT);

        } catch (Exception e) {
            logger.error("Init socket server fail:",e);
            // 关闭socket服务
            closeSocketServer();
            return false;
        }
        return true;
    }

    public void startSocketServer(){

    }

    private void closeSocketServer(){

    }


}

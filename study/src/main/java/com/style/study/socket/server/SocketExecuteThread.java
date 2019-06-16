package com.style.study.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket服务线程：接受并分发客户端的socket。
 */
public class SocketExecuteThread implements Runnable {

    private boolean shutdownServer;

    private ServerSocket serverSocket;

    private SocketMsgDispatcher dispatcher = new SocketMsgDispatcher();

    public SocketExecuteThread(ServerSocket serverSocket ,boolean shutdownServer){
        this.serverSocket = serverSocket;
        this.shutdownServer = shutdownServer;
    }

    @Override
    public void run() {

        while(!shutdownServer){
            try {
                // 从请求队列中获取socket，默认请求队列大小50。
                Socket socket = this.serverSocket.accept();
                dispatcher.dispatchSocketMessage(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}


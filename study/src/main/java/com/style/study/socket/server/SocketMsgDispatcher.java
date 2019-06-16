package com.style.study.socket.server;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息分发类，将socket消息添加到线程池中处理
 */
public class SocketMsgDispatcher {

    private static ThreadPoolExecutor threadPool;

    private static BlockingQueue<Runnable> workQueue;

    static{
        workQueue =  new LinkedBlockingQueue<>(100);
        threadPool = new ThreadPoolExecutor(50,
                50,
                1000,
                TimeUnit.MILLISECONDS,
                workQueue);
    }


    public SocketMsgDispatcher(){

    }


    /**
     * 分发Socket消息
     * @param socket
     */
    public void dispatchSocketMessage(Socket socket){

        // 将Socket放入线程池中进行处理

        SocketMsgProcessor processor = new SocketMsgProcessor(socket);

        threadPool.execute(processor);

    }
}

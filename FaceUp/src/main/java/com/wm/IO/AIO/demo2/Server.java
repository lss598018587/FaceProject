package com.wm.IO.AIO.demo2;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class Server {
    private final static int DEFAULT_PORT = 6789;
    private static AsyncServerHandler serverHandler;
    public volatile static long clientCount = 0;

    public static void start(){
        start(DEFAULT_PORT);
    }

    public static synchronized void start(int port){
        if(serverHandler!=null)return;
        serverHandler = new AsyncServerHandler(port);
        new Thread(serverHandler,"miao miao").start();
    }

    public static void main(String[] args) {
        start();
    }
}

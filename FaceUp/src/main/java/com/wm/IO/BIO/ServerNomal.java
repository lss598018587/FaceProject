package com.wm.IO.BIO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/29 .
 */
public class ServerNomal {
    private final static int DEFAULT_PORT = 8765;

    private static ServerSocket serverSocket;

    public static void start () throws IOException {
        start(DEFAULT_PORT);
    }
    public synchronized static void start(int port) throws IOException {
        if(serverSocket!=null) return;
        try {
            //通过构造函数创建ServerSocket
            //如果端口合法且空闲，服务端就监听成功
            //这个50就说backlog，指队列长度，允许接入的client
            serverSocket = new ServerSocket(port,50);
            System.out.println("服务器已启动，端口号：" + port);
            //通过无线循环监听客户端连接
            //如果没有客户端接入，将阻塞在accept操作上。
//            while (true){
                //阻塞，等待client发来请求
                Socket socket =serverSocket.accept();

                //当有新的客户端接入时，会执行下面的代码
                //然后创建一个新的线程处理这条Socket链路
                new Thread(new ServerHandler(socket)).start();
//            }
        }finally {
            if(serverSocket!=null){
                serverSocket.close();
            }
            serverSocket=null;
        }

    }

    public static void main(String[] args) {
        try {
            ServerNomal.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

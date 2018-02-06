package com.wm.IO.NIO;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/29 .
 */
public class Server {
    private static final int PORT=6789;
    private static ServerHandle serverHandle;
    public static void start(){
        if(serverHandle!=null) serverHandle.stop();
        serverHandle = new ServerHandle(PORT);
        new Thread(serverHandle,"喵喵的线程").start();
    }

    /**
     *       打开ServerSocketChannel，监听客户端连接
             绑定监听端口，设置连接为非阻塞模式
             创建Reactor线程，创建多路复用器并启动线程
             将ServerSocketChannel注册到Reactor线程中的Selector上，监听ACCEPT事件
             Selector轮询准备就绪的key
             Selector监听到新的客户端接入，处理新的接入请求，完成TCP三次握手，简历物理链路
             设置客户端链路为非阻塞模式
             将新接入的客户端连接注册到Reactor线程的Selector上，监听读操作，读取客户端发送的网络消息
             异步读取客户端消息到缓冲区
             对Buffer编解码，处理半包消息，将解码成功的消息封装成Task
             将应答消息编码为Buffer，调用SocketChannel的write将消息异步发送给客户端
     * @param args
     */
    public static void main(String[] args) {
        start();
    }


}

package com.wm.IO.AIO.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangmiao on 2018/1/30.
 */
public class Server {
    //线程池
    private ExecutorService executor;
    //线程组
    private AsynchronousChannelGroup threadGroup;
    //服务器通道
    public AsynchronousServerSocketChannel assc;

    public Server(int port) {
        try {
            //创建一个缓存池
            executor = Executors.newCachedThreadPool();
            //创建线程组
            threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executor, 1);
            //创建服务器通道
            assc = AsynchronousServerSocketChannel.open();
            //进行绑定
            assc.bind(new InetSocketAddress(port));

            System.out.println("server start , port :" + port);
            //进行监听
            assc.accept(this, new ServerHandler());
            //不让服务器关闭，这个监听是异步的
            Thread.sleep(Integer.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static void main(String[] args) {
        Server server = new Server(6789);
    }
}

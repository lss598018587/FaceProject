package com.wm.IO.AIO.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class AsyncServerHandler implements Runnable  {

    public AsynchronousServerSocketChannel channel;

    public CountDownLatch latch;

    public AsyncServerHandler(int port) {
        try {
            //创建异步服务通道
            channel = AsynchronousServerSocketChannel.open();
            //绑定端口号
            channel.bind(new InetSocketAddress(port));
            System.out.println("喵喵的服务已启动");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        try {
            /**
             * 一直阻塞着，让异步监听一直持续着
             * 防止服务端运行完退出程序
             * 也可以用Thread.sleep(Integer.max)或while（true）
             */

            latch = new CountDownLatch(1);
            channel.accept(this,new AcceptHandler());
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

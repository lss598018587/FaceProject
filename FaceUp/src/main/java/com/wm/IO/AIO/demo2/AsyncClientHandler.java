package com.wm.IO.AIO.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class AsyncClientHandler implements Runnable,CompletionHandler<Void,AsyncServerHandler> {

    private AsynchronousSocketChannel channel;
    private String host;
    private int port;

    private CountDownLatch latch;

    public AsyncClientHandler(String ip,int port) {
        try {
            this.host = ip;
            this.port =port;
            //创建异步的客户端通道
            channel = AsynchronousSocketChannel.open();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        channel.connect(new InetSocketAddress(host,port));
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg){
        byte[] bytes = msg.getBytes();
        ByteBuffer writeBuffer  = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        //异步写入
        channel.write(writeBuffer,writeBuffer,new ClientWriteHandler(channel,latch));
    }

    @Override
    public void completed(Void result, AsyncServerHandler attachment) {
        System.out.println("喵喵客户端连接成功");
    }

    @Override
    public void failed(Throwable exc, AsyncServerHandler attachment) {
        exc.printStackTrace();
        try {
            channel.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

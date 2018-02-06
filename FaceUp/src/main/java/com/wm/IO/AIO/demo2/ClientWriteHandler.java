package com.wm.IO.AIO.demo2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ClientWriteHandler implements CompletionHandler<Integer,ByteBuffer>{

    private AsynchronousSocketChannel channel;
    private CountDownLatch latch;

    public ClientWriteHandler(AsynchronousSocketChannel channel, CountDownLatch latch) {
        this.channel = channel;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if(buffer.hasRemaining()){
            channel.write(buffer,buffer,this);
        }else{
            //读取数据
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            channel.read(byteBuffer,byteBuffer,new ClientReadHandler(channel,latch));
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println("发送数据失败...");
        try {
            channel.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.wm.IO.AIO.demo2;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncServerHandler> {
    @Override
    public void completed(AsynchronousSocketChannel channel, AsyncServerHandler attachment) {
        Server.clientCount++;
        System.out.println("客户端的链接数："+Server.clientCount);
        attachment.channel.accept(attachment,this);

        //创建新的Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //异步读，第三个是处理类
        channel.read(buffer,buffer,new ReadHandler(channel));
    }

    @Override
    public void failed(Throwable exc, AsyncServerHandler serverHandler) {
        exc.printStackTrace();
        serverHandler.latch.countDown();
    }

}

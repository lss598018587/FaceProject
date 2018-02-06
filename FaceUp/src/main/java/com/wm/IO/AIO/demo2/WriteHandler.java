package com.wm.IO.AIO.demo2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class WriteHandler implements CompletionHandler<Integer,ByteBuffer> {

    private AsynchronousSocketChannel channel;

    public WriteHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        //如果缓存里还有东西就把发送完
        if(attachment.hasRemaining()){
            channel.write(attachment,attachment,this);
        }else{
            //创建新的buffer
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
            //异步读  第三个参数为接收消息回调的业务Handler
            channel.read(writeBuffer, writeBuffer, new ReadHandler(channel));
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

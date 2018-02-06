package com.wm.IO.AIO.demo2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ReadHandler implements CompletionHandler<Integer,ByteBuffer> {
    private AsynchronousSocketChannel socketChannel;

    public ReadHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        //flip操作
        attachment.flip();
        byte[] bytes= new byte[attachment.remaining()];
        attachment.get(bytes);
        try {
            String rs = new String(bytes,"utf-8");
            System.out.println("服务端接受的结果："+rs);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        String response = "返回给客户段的数字为"+random.nextInt(10000);
        doWrite(response);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doWrite(String result){
        byte[] bytes = result.getBytes();
        ByteBuffer byteWrite = ByteBuffer.allocate(bytes.length);
        byteWrite.put(bytes);
        byteWrite.flip();
        socketChannel.write(byteWrite,byteWrite,new WriteHandler(socketChannel));
    }
}

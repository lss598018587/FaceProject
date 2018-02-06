package com.wm.IO.AIO.demo1;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangmiao on 2018/1/30.
 */
public class ServerHandler implements CompletionHandler<AsynchronousSocketChannel,Server> {
    @Override
    public void completed(AsynchronousSocketChannel asc, Server attachment) {
        //当有下一个客户端接入当时候，直接调用Service的accept方法，这样反复执行下去，保证多个客户端都可以阻塞
        //相当于上一个监听用完了，就没了，这里再调用这个方法，再次监听
        attachment.assc.accept(attachment,this);
        read(asc);
    }

    @Override
    public void failed(Throwable exc, Server attachment) {
        exc.printStackTrace();
    }
    public void read(final AsynchronousSocketChannel asc){
        //读取数据
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        asc.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer resultSize, ByteBuffer attachment) {
                //进行读取之后，重置标识位
                attachment.flip();
                //获取读取的字节数
                System.out.println("获取的客户端数据长度为："+resultSize);
                //
                String result = new String(attachment.array()).trim();
                System.out.println("获取客户端的信息为："+result);
                Random random = new Random();
                String response = "返回给客户段的数字为"+random.nextInt(10000);

                Write(asc,response);

            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });
    }

    /**
     *
     * @param asc
     * @param response
     */
    public void Write(AsynchronousSocketChannel asc,String response){
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(response.getBytes());
            buffer.flip();
            asc.write(buffer).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {

        }
    }
}

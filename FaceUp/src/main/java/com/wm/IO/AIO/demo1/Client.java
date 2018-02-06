package com.wm.IO.AIO.demo1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangmiao on 2018/1/30.
 */
public class Client implements Runnable {


    private AsynchronousSocketChannel asc;

    public Client() throws IOException {
        asc = AsynchronousSocketChannel.open();
    }
    public void connect(){
        asc.connect(new InetSocketAddress("127.0.0.1",6789));
    }
    public  void write(String response){
        try {
//            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
//            buffer.flip();
            asc.write(ByteBuffer.wrap(response.getBytes())).get();
            read();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public  void read(){
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            //读取请求码流，返回读取到的字节数
            int readBytes = asc.read(buffer).get();
            //读取到字节，对字节进行编解码
            if(readBytes>0){

                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                System.out.println("服务端返回的信息："+new String(bytes,"utf-8").trim());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true){

        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client c1 = new Client();
        c1.connect();

        Client c2 = new Client();
        c2.connect();

        Client c3 = new Client();
        c3.connect();

        new Thread(c1).start();
        new Thread(c2).start();
        new Thread(c3).start();

        Thread.sleep(1000);

        c1.write("c1 aaa");
        c2.write("c2 bbb");
        c3.write("c3 ccc");
    }
}

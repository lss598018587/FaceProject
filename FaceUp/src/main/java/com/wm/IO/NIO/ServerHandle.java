package com.wm.IO.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/29 .
 */
public class ServerHandle implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean started;

    public ServerHandle(int port) {
        try{
            //创建选择器
            selector = Selector.open();
            //打开监听通道
            serverSocketChannel = ServerSocketChannel.open();
            //如果为true则为阻塞，如果为false就为非阻塞
            serverSocketChannel.configureBlocking(false);
            //绑定端口 backlog设为1024  输入连接指示（对连接的请求）的最大队列长度被设置为 backlog 参数。如果队列满时收到连接指示，则拒绝该连接。
//            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            serverSocketChannel.bind(new InetSocketAddress(port),1024);
            //监听客户端连接请求 (SelectionKey.OP_ACCEPT>> 接收连接继续事件，表示服务器监听到了客户连接，服务器可以接收这个连接了)
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //标记服务器已开启
            started = true;
            System.out.println("服务器已启动，端口号为"+port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        //循环遍历selector
        while (started){
            try{
                //无论是否有读写事件发生，selector每隔1s被唤醒一次
                selector.select(1000);
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key = null;
                while (it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    }catch (Exception e){
                        if(key!=null){
                            key.cancel();
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //selector关闭后会自动释放里面管理的资源
        if(selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        started = false;
    }

    public void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            //处理新接入的请求消息
            if(key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                //通过ServerSocketChannel的accept创建SocketChannel实例
                //完成该操作意味着完成TCP三次握手，TCP物理链正式建立
                SocketChannel sc = ssc.accept();
                //设置为非阻塞的
                sc.configureBlocking(false);
                //注册为读(OP_READ —— 读就绪事件，表示通道中已经有了可读的数据，可以执行读操作了（通道目前有数据，可以进行读操作了）)
                sc.register(selector,SelectionKey.OP_READ);
            }
            //读消息
            if(key.isReadable()){
                SocketChannel sc = (SocketChannel)key.channel();
                //创建ByteBuffer，并开辟一个1M的缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                //读取请求码流，返回读取到的字节数
                int readBytes = sc.read(buffer);
                //读取到字节，对字节镜像编解码
                if(readBytes>0){
                    //将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
                    buffer.flip();
                    //根据缓冲区读字节数创建字节数组
                    byte[] bytes = new byte[buffer.remaining()];
                    //将缓冲区可读字节数组复制到新建的数组中
                    buffer.get(bytes);
                    String msg = new String (bytes,"UTF-8");
                    System.out.println("服务器收到消息："+msg);
                    Random random = new Random();
                    //处理数据
                    String result = "数字为："+random.nextInt(2000);
                     //发送应答消息
                    doWrite(sc,result);
                }
                //链路已经关闭，释放资源
                else if(readBytes<0){
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    //异步发送应答消息
    private void doWrite(SocketChannel channel ,String response) throws IOException {
        //将消息编码为字节数组
        byte[]bytes = response.getBytes();
        //根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        //将字节数组复制到缓冲区
        writeBuffer.put(bytes);
        //flip 操作
//        writeBuffer.flip();
        //发送缓冲区的字节数组
        channel.write(writeBuffer);
    }
}

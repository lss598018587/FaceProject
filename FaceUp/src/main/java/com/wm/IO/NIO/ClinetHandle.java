package com.wm.IO.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/29 .
 */
public class ClinetHandle implements Runnable{

    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean started;
    private String host;
    private int port;

    public   void stop(){
        started = false;
    }

    public ClinetHandle(String ip,int port) {
        this.host = ip;
        this.port  = port;
        try {
            //创建选择器
            selector = Selector.open();
            //打开监听通道
            socketChannel = SocketChannel.open();
            //如果为true则此通道将被治愈阻塞模式；如果为false，则此通道将被置于非阻塞模式
            socketChannel.configureBlocking(false); //开启非阻塞模式
            started= true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
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
                    try{
                        handleInput(key);
                    }catch (Exception e){
                        if(key !=null){
                            key.cancel();
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            SocketChannel sc = (SocketChannel) key.channel();
            if(key.isConnectable()){    //测试此键的通道是否已完成其套接字连接操作。
                if(sc.finishConnect()); //主要作用就是确认通道连接已建立
                else System.exit(1);
            }
            //读消息
            if(key.isReadable()){
                //创建ByteBuffer，并开启一个1M的缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                //读取请求码流，返回读取到的字节数
                int readBytes = sc.read(buffer);
                //读取到字节，对字节进行编解码
                if(readBytes>0){
                    //将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
                    buffer.flip();
                    //根据缓冲区可读字节数创建字节数组
                    byte[] bytes = new byte[buffer.remaining()];
                    //将缓冲区可读字节数组复制到新建的数组中
                    buffer.get(bytes);
                    String result = new String(bytes,"UTF-8");
                    System.out.println("客户端收到消息："+result);

                }
                else if(readBytes<0){
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    //异步发送消息
    public void doWrite(SocketChannel channel,String request) throws IOException {
        //将消息编码为字节数组
        byte[] byteps = request.getBytes();
        //根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(byteps.length);
        //将字节数组复制到缓冲区
        writeBuffer.put(byteps);
        //flip操作
        writeBuffer.flip();
        //发送缓冲区的字节数组
        channel.write(writeBuffer);
    }
    public void doConnect() throws IOException {
        if(socketChannel.connect(new InetSocketAddress(host,port)));
        //OP_CONNECT —— 连接就绪事件，表示客户与服务器的连接已经建立成功
        else socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }
    public void sendMsg(String msg) throws IOException {
        socketChannel.register(selector,SelectionKey.OP_READ);
        doWrite(socketChannel,msg);
    }
}

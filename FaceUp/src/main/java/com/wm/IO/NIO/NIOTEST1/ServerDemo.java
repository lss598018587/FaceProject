package com.wm.IO.NIO.NIOTEST1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @Auther: miaomiao
 * @Date: 2019-05-09 19:22
 * @Description:
 */
public class ServerDemo {
    private ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocateDirect(1024);
    private Selector selector;

    public ServerDemo() throws IOException{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8080));
        System.out.println("listening on port 8080");

        this.selector = Selector.open();;

        // 绑定channel的accept
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public static void main(String[] args) throws Exception{
        new ServerDemo().go();
    }

    private void go() throws Exception{

        System.out.println("------------------进来几次----------------------");
        // block api
        while(true){
            int numkeys = selector.select();
            if(numkeys<=0){
                break;
            }
            System.out.println("selector.select()>>>>>"+numkeys);
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                SelectableChannel ch = selectionKey.channel();
//                try {
//                    if (ch instanceof DatagramChannel && !ch.isOpen() ||
//                            ch instanceof SocketChannel && !((SocketChannel) ch).isConnected()) {
//                        //发现了关闭的通道赶紧取消以防万一，不会再下次select的key集合中
//                        selectionKey.cancel();
//                        break;
//                    }
//                } catch (CancelledKeyException e) {
//                    // ignore
//                }

                // 新连接
                if(selectionKey.isAcceptable()){
                    System.out.println("第一步");
                    ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();

                    // 新注册channel
                    SocketChannel socketChannel  = server.accept();
                    if(socketChannel==null){
                        continue;
                    }
                    socketChannel.configureBlocking(false);
                    // 注意！这里和阻塞io的区别非常大，在编码层面之前的等待输入已经变成了注册事件，这样我们就可以在等待的时候做别的事情，
                    // 比如监听更多的socket连接，也就是之前说了一个线程监听多个socket连接。这也是在编码的时候最直观的感受
//                    socketChannel.register(selector, SelectionKey.OP_READ| SelectionKey.OP_WRITE);
                    socketChannel.register(selector, SelectionKey.OP_READ);


//                    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
//                    buffer.put("hi new channel".getBytes());
//                    buffer.flip();
//                    socketChannel.write(buffer);

                }

                // 服务端关心的可读，意味着有数据从client传来了，根据不同的需要进行读取，然后返回
                if(selectionKey.isReadable()){
                    System.out.println("第二步");
                    System.out.println("isReadable");
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

                    readBuffer.clear();
                    int read = socketChannel.read(readBuffer);
                    /**
                     * nio的客户端如果关闭了，服务端还是会收到该channel的读事件，
                     * 但是数目为0，而且会读到-1，其实-1在网络io中就是socket关闭的含义，
                     * 在文件时末尾的含义，所以为了避免客户端关闭服务端一直收到读事件，必须检测上一次的读是不是-1，如果是-1，就关闭这个channel。
                     */
                    System.out.println("可读大小："+read);
                    if(read == -1){
                        System.out.println("断开");
                        socketChannel.close();
                    }

                    readBuffer.flip();

                    String receiveData= Charset.forName("UTF-8").decode(readBuffer).toString();
                    System.out.println("receiveData:"+receiveData);

                    // 把读到的数据绑定到key中
                    selectionKey.attach("server message echo:"+receiveData);


                }

                System.out.println("第三步");


                // 实际上服务端不在意这个，这个写入应该是client端关心的，这只是个demo,顺便试一下selectionKey的attach方法
                if(selectionKey.isWritable()){
                    System.out.println("第四步");
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

                    String message = (String) selectionKey.attachment();
                    if(message==null){
                        selectionKey.interestOps(SelectionKey.OP_READ);
                        continue;
                    }
                    selectionKey.attach(null);

                    writeBuffer.clear();
                    writeBuffer.put(message.getBytes());
                    writeBuffer.flip();
                    while(writeBuffer.hasRemaining()){
                        socketChannel.write(writeBuffer);
                    }
                }

                System.out.println();
            }
        }
    }
}

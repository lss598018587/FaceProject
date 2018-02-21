package com.wm.IO.Netty.demo1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class Server {

    private final static int port =6789;

    public static void main(String[] args) throws InterruptedException {
        //第一个线程组用于接受Client端连接的
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //第二个线程组是用于实际的业务处理操作的
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //创建一个辅助类Bootstrap,就是对我们的Server进行一系列的配置
            ServerBootstrap b = new ServerBootstrap();
            //把两个工作线程组加入进来
            b.group(bossGroup, workGroup)
                    //指定使用通道
                    .channel(NioServerSocketChannel.class)
                    //绑定具体的处理事件
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerHandler()); //在这里配置具体数据接收方的处理
                        }
                    })
                    /**
                     * 服务器端TCP内核模块有个队列，我们称之为A,B
                     * 客户端向服务端connect的时候，会发送带有SYN标志的包（第一次握手）
                     * 服务器收到客户端发来的SYN时，向客户端发送SYN，ACK确认（第二次握手）
                     * 此时TCP内核模块吧客户端链接加入到A队列中，然后服务器收到客户端发来的ACK时（第三次握手）
                     * TCP内核模块吧客户端链接从A队列一到B队列，链接完成，应用程序的accept会返回
                     * 也就是说accept从B队列中取出完成三次握手的链接。
                     * A队列和B队列的长度之后就是backlog，当A，B队列的长度之后大于backlog时，新链接将会被TCP内核拒绝
                     * 所以，如果backlog过小，可能会出现accept链接跟不上，A,B队列满了，导致新的客户端无法链接
                     * 要注意的是：backlog对程序支持的连接数并无影响，backlog影响的只是还没有被accept取出的连接数。
                     */
                    .option(ChannelOption.SO_BACKLOG, 128)  //设置tcp缓冲区
                    .option(ChannelOption.SO_SNDBUF,32*2014) //设置发送缓冲大小
                    .option(ChannelOption.SO_RCVBUF,32*2014) //接受缓冲大小

                    .childOption(ChannelOption.SO_KEEPALIVE, true);//  是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。

            //指定端口，进行监听—————sync()方法来阻塞当前线程，直到异步操作执行完毕
            ChannelFuture future = b.bind(port).sync();
            ChannelFuture f = b.bind(1234).sync();
            //等待关闭，就一直阻塞的，连接通信，只有程序调用退出，才退出
            future.channel().closeFuture().sync();
            System.out.println("123");
            f.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}

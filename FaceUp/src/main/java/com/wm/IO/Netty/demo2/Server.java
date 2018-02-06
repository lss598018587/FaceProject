package com.wm.IO.Netty.demo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

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
                            /**
                             * 这个分割符，只能放在最后
                             * 如：王淼_@最帅  会有两条信息   1.王淼 和 2.最帅
                             * 如：王淼最帅_@  会有一条信息   1.王淼最帅
                             */
                            ByteBuf buf = Unpooled.copiedBuffer("_@".getBytes());
                            //第一个参数指导分割符长度
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf));
                            //设置字符串形式的解码
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ServerHandler()); //在这里配置具体数据接收方的处理
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)  //设置tcp缓冲区
                    .option(ChannelOption.SO_SNDBUF,32*2014) //设置发送缓冲大小
                    .option(ChannelOption.SO_RCVBUF,32*2014) //接受缓冲大小

                    .childOption(ChannelOption.SO_KEEPALIVE, true);//  是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。

            //指定端口，进行监听—————sync()方法来阻塞当前线程，直到异步操作执行完毕
            ChannelFuture future = b.bind(port).sync();

            //等待关闭，就一直阻塞的，连接通信，只有程序调用退出，才退出,,之后才会继续执行下去
            future.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}

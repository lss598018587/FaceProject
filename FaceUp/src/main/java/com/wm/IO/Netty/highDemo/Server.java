package com.wm.IO.Netty.highDemo;

import com.wm.IO.Netty.highDemo.decode.RemotingTransporterDecoder;
import com.wm.IO.Netty.highDemo.encode.RemotingTransporterEncoder;
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

        ServerHighHandler server = new ServerHighHandler();
       final ServerHighHandler.NettyServerHandler serverHandler  = server.new NettyServerHandler();

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
                            ch.pipeline().addLast(new RemotingTransporterDecoder());
                            ch.pipeline().addLast(new RemotingTransporterEncoder());
                            ch.pipeline().addLast(serverHandler); //在这里配置具体数据接收方的处理
                        }
                    });

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

package com.wm.IO.Netty.demo1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class Client  {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new ClientHandler());
                    }
                });

        //绑定多个端口
        ChannelFuture f = b.connect("127.0.0.1",6789).sync();
        ChannelFuture f1 = b.connect("127.0.0.1",1234).sync();

        f.channel().writeAndFlush(Unpooled.copiedBuffer("王淼最帅".getBytes()));
        f1.channel().writeAndFlush(Unpooled.copiedBuffer("王淼最帅22222222".getBytes()));
//        f1.channel().writeAndFlush(Unpooled.copiedBuffer("王淼最帅".getBytes()));


        f.channel().closeFuture().sync();
        f1.channel().closeFuture().sync();
        workGroup.shutdownGracefully();
    }
}

package com.wm.IO.Netty.runtime;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author:wangmiao
 * @Desc client或服务器端，挂了的话，怎么办
 * @Date Created in   2018/2/5 .
 */
public class Client  {

    public static class ChildrenClient{
        final static Client client =new Client();
    }

    public static Client getInstanceClient(){
        return ChildrenClient.client;
    }
    private EventLoopGroup workGroup;
    private  Bootstrap b;
    private ChannelFuture f;

    public Client() {
        workGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(workGroup)
                .channel(NioSocketChannel.class)
                //设置日志
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingDecoder());
                        sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder());
                        //超市handler（当服务器和客户端在制定时间以上没有任何通信，则会关闭响应通道，主要减小服务端资源占用）
                        //5s后没有通信就断掉
                        sc.pipeline().addLast(new ReadTimeoutHandler(3));
                        sc.pipeline().addLast(new ClientHandler());
                    }
                });
    }

    public void connect(){
        try {
            this.f = b.connect("127.0.0.1",6789).sync();
            System.out.println("远程服务器已连接，可以进行数据交换");


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public ChannelFuture getChannelFuture(){
        if(this.f ==null){
            this.connect();
        }
        if(!this.f.channel().isActive()){
            this.connect();
        }
        return this.f;
    }

    public void close(){
        workGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        final Client c = Client.getInstanceClient();
        c.connect();
        ChannelFuture f= c.getChannelFuture();
        for (int i = 0; i <3 ; i++) {
            Req r = new Req();
            r.setId(i+"");
            r.setName("wm"+i);
            r.setRequestMessage("yiyi"+i);
            f.channel().writeAndFlush(r);
            TimeUnit.SECONDS.sleep(4);
        }

        f.channel().closeFuture().sync();
        System.out.println("正常时间段，发送结束了");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("进入子程序。。。。");
//                ChannelFuture cf = c.getChannelFuture();
//                System.out.println("isActive>>"+cf.channel().isActive());
//                System.out.println("isOpen>>"+cf.channel().isOpen());
//
//                //再次发送数据
//                Req req = new Req();
//                req.setId("9999");
//                req.setName("miaom");
//                req.setRequestMessage(Thread.currentThread().getName());
//                cf.channel().writeAndFlush(req);
//                try {
//                    cf.channel().closeFuture().sync();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("线程结束");
//            }
//        },"喵淼子线程").start();


        System.out.println("客户端结束了---关闭");
//        c.close();
    }
}

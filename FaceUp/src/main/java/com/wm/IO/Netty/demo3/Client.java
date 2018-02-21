package com.wm.IO.Netty.demo3;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class Client  {

    public static void main(String[] args) throws Exception {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingDecoder());
                        sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder());
                        sc.pipeline().addLast(new ClientHandler());
                    }
                });

        //绑定多个端口
        ChannelFuture f = b.connect("127.0.0.1",6789).sync();

        for (int i = 0; i <5 ; i++) {
            Req r = new Req();
            r.setId(i+"");
            r.setName("wm"+i);
            r.setRequestMessage("yiyi"+i);
            String readPath = System.getProperty("user.dir")+ File.separatorChar+ "FaceUp"+File.separatorChar+"sources"+File.separatorChar+"ccc.jpg";
            File file = new File(readPath);
            FileInputStream in = new FileInputStream(file);
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            r.setAttachment(GzipUtils.gzip(data));
            f.channel().writeAndFlush(r);
        }

        f.channel().closeFuture().sync();
        workGroup.shutdownGracefully();
    }
}

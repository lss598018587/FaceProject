package com.wm.IO.Netty.highDemo;

import com.wm.IO.Netty.highDemo.modal.RemotingTransporter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ServerHighHandler extends NettyRemotingBase {

//extends NettyRemotingBase

    class NettyServerHandler extends ChannelHandlerAdapter{

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
            System.out.println("服务端收到信息了！！！！");
            processMessageReceived(ctx, (RemotingTransporter)msg);
        }
    }

}

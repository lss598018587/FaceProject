package com.wm.IO.Netty.highDemo;

import com.wm.IO.Netty.highDemo.modal.RemotingTransporter;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ClientHighHandler extends NettyRemotingBase  {

//extends

    public class NettyClientHandler  extends ChannelHandlerAdapter {

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//            processChannelInactive(ctx);
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
            System.out.println("client收到服务端信息了------");
            processMessageReceived(ctx, (RemotingTransporter)msg);
        }
    }
}

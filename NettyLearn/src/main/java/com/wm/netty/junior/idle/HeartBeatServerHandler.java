package com.wm.netty.junior.idle;



import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: miaomiao
 * @Date: 18/6/21 14:42
 * @Description:
 */
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server channelRead..");
        SimpleDateFormat format = new SimpleDateFormat("hh:MM:ss");
        String time = format.format(new Date());
        System.out.println(ctx.channel().remoteAddress() + "time:"+time+"->Server :" + msg.toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client端断开连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}

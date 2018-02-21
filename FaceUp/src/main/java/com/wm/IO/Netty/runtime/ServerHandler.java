package com.wm.IO.Netty.runtime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channel active...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            Req req = (Req) msg;
            System.out.printf("服务端接受的数据id:%s \r\n,name:%s \r\n,msg:%s  \r\n",req.getId(),req.getName(),req.getRequestMessage());

            Resp resp = new Resp();
            resp.setId("respId"+req.getId());
            resp.setName("respName"+req.getId());
            resp.setResponseMessage("msg"+req.getRequestMessage());
//            ChannelFuture f =ctx.writeAndFlush(resp);
            ctx.writeAndFlush(resp);
            //自动监听，客户端发过来的请求已写入完毕后断开链接
//            f.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}

package com.wm.IO.Netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ServerHandler extends ChannelHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // Do something with msg
            ByteBuf byteBuf = (ByteBuf)msg;

            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String request = new String(bytes,"UTF-8");
            System.out.println("服务端接受的数据："+request);

            Random random = new Random();
            String fanhui = "服务器返回的数据为："+random.nextInt(1000);


            ChannelFuture f =ctx.writeAndFlush(Unpooled.copiedBuffer(fanhui.getBytes()));

            //自动监听，客户端发过来的请求已写入完毕后断开链接
            f.addListener(ChannelFutureListener.CLOSE);
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

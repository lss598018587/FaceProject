package com.wm.IO.Netty.heartToHeart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ServerHeartBeatHandler extends ChannelHandlerAdapter {

    private static HashMap<String,String> AUTH_IP_MAP = new HashMap<>();

    private static final String SUCCESS_KEY = "auth_success_key";

    static {
        AUTH_IP_MAP.put("192.168.1.109","1234");
    }

    private boolean auth(ChannelHandlerContext ctx,Object msg){
        String [] ret = ((String)msg).split(",");
        String auth = AUTH_IP_MAP.get(ret[0]);
        if(auth !=null && auth.equals(ret[1])){
            ctx.writeAndFlush(SUCCESS_KEY);
            return true;
        }else {
            ctx.writeAndFlush("auth failure !!").addListener(ChannelFutureListener.CLOSE);
            return false;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channel active...");
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if(msg instanceof String){
                System.out.println("client send msg"+msg);
                auth(ctx,msg);
            }else if(msg instanceof RequestInfo){
                RequestInfo info =  (RequestInfo)msg;
                System.out.println("-----------------");
                System.out.println("当前主机ip："+info.getIp());
                Map<String,String> cpu = info.getCpuPerc();
                System.out.println("系统使用率:"+cpu.get("sys"));

                System.out.println("当前主机当memory情况：");
                Map<String,String> memory = info.getMemory();
                System.out.println("内存总量："+memory.get("total"));
                System.out.println("-----------------");

                ctx.writeAndFlush("info received");
            }else{
                ctx.writeAndFlush("connect failure!!").addListener(ChannelFutureListener.CLOSE);
            }
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

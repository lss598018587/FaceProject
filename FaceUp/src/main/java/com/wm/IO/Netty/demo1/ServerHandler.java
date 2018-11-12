package com.wm.IO.Netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ServerHandler extends ChannelHandlerAdapter {

    private static final AttributeKey<Wang> S_PUBLISH_KEY = AttributeKey.valueOf("wangmiao");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("进到服务的端了");
        Attribute<Wang> wang = ctx.channel().attr(S_PUBLISH_KEY);

//        Wang t2 = wang.get();

//        if(t2==null){
            Wang wang1 = new Wang();
            wang1.setId("mjj");
            wang1.setName("456111111");
            //里面没值，放进去，返回null
            //里面有值，放不进去。返回之前已存在的值
            Wang t1 = wang.setIfAbsent(wang1);
            if(t1==null){
                System.out.println("wang1放进去，返回null");
            }else{
                System.out.println("wang1放进去，返回对象，t1"+t1);
                System.out.println("现在里面的值"+wang.get());
            }
//        }else{
//            System.out.println("有值1："+t2);
//        }
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("进入到刚连接的地方");
        Attribute<Wang> wang = ctx.channel().attr(S_PUBLISH_KEY);

        Wang t2 = wang.get();

        if(t2==null ){
            Wang wang1 = new Wang();
            wang1.setId("123");
            wang1.setName("456");
              t2 = wang.setIfAbsent(wang1);
            if(t2==null){
                System.out.println("qwe");
                t2 = wang1;
                t2.setName("qqqqqqqqqqq");
                System.out.println("qqqqqqqqq>>"+wang.get());
            }else{
                System.out.println(t2);
            }
        }else{
            System.out.println("有值2："+wang);
        }
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    class Wang {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Wang{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}

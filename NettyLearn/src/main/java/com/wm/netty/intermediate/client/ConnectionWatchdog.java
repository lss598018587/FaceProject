package com.wm.netty.intermediate.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: miaomiao
 * @Date: 18/6/21 14:43
 * @Description:
 *
 * 重连检测狗，当发现当前的链路不稳定关闭之后，进行12次重连
 *
 * ①TimeTask，我们就要写run方法，这应该是一个定时任务，这个定时任务做的事情应该是重连的工作
 *
 * ②ChannelHandlerHolder的接口，这个接口我们刚才说过是维护的所有的Handlers，因为在重连的时候需要获取Handlers
 *
 * bootstrap对象，重连的时候依旧需要这个对象
 *
 * 当链路断开的时候会触发channelInactive这个方法，也就说触发重连的导火索是从这边开始的
 */
@Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask ,ChannelHandlerHolder {


    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;


    private boolean firstConnection = true;
    private volatile SocketAddress remoteAddress;

    private final String host;

    private volatile boolean reconnect = true;
    private int attempts;


    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port,String host, boolean reconnect) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
    }


    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }


    /**
     * channel链路每次active的时候，将其连接的次数重新☞ 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("当前链路已经激活了，重连尝试次数重新置为0");

        attempts = 0;
        //都是一个channel的ChannnelActive方法中调用fireChannelActive来触发调用下一个handler中的ChannelActive方法
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("链接关闭");
        if(reconnect){

            System.out.println("链接关闭，将进行重连");
            if (attempts < 12) {
                attempts++;
                //重连的间隔时间会越来越长
                int timeout = 2 << attempts;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }
        ctx.fireChannelInactive();
    }


    public void run(Timeout timeout) throws Exception {

        ChannelFuture future;
        //bootstrap已经初始化好了，只需要将handler填入就可以了
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {

                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host,port);
        }
        //future对象
        future.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture f) throws Exception {
                boolean succeed = f.isSuccess();

                //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
                if (!succeed) {
                    System.out.println("重连失败");
                    f.channel().pipeline().fireChannelInactive();
                }else{
                    System.out.println("重连成功");
                }
            }
        });

    }

}

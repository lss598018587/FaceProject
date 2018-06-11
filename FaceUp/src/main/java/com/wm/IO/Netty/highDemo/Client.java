package com.wm.IO.Netty.highDemo;

import com.wm.IO.Netty.demo1.ClientHandler;
import com.wm.IO.Netty.highDemo.decode.RemotingTransporterDecoder;
import com.wm.IO.Netty.highDemo.encode.RemotingTransporterEncoder;
import com.wm.IO.Netty.highDemo.modal.RemotingResponse;
import com.wm.IO.Netty.highDemo.modal.RemotingTransporter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.wm.IO.Netty.highDemo.TestCommonCustomBody.ComplexTestObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class Client  {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);


    protected static volatile ByteBufAllocator allocator;

    public static final byte TEST = -1;


    public static void main(String[] args) throws InterruptedException {
        //内部类实例化
        ClientHighHandler clientHighHandler =new ClientHighHandler();
//        final ClientHighHandler.NettyClientHandler nettyClientHandler = clientHighHandler.new NettyClientHandler();

        EventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new RemotingTransporterDecoder());
                        sc.pipeline().addLast(new RemotingTransporterEncoder());
                        sc.pipeline().addLast(clientHighHandler.new NettyClientHandler());
                    }
                });

        //绑定多个端口
        ChannelFuture f = b.connect("127.0.0.1",6789).sync();
        ComplexTestObj complexTestObj = new ComplexTestObj("attr1", 2);
        TestCommonCustomBody commonCustomHeader = new TestCommonCustomBody(1, "test",complexTestObj);
        RemotingTransporter request = RemotingTransporter.createRequestTransporter(TEST, commonCustomHeader);
//        f.channel().writeAndFlush(request);
//        f.channel().closeFuture().sync();
//        workGroup.shutdownGracefully();

        //绑定多个端口
        final Channel channel = f.channel();
        if(channel!=null && channel.isActive()){

            RemotingTransporter response =  invokeSyncImpl(channel, request, 30000);
            System.out.println(response);
        }else{
            channel.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.printf("closeChannel: close the connection to remote address[%s] result: {%s}",
                            future.isSuccess());
                }
            });
            workGroup.shutdownGracefully();
        }
    }
    public static RemotingTransporter invokeSyncImpl(final Channel channel, final RemotingTransporter request, final long timeoutMillis)throws   InterruptedException{
        try {
            //构造一个请求的封装体，请求Id和请求结果一一对应
            final RemotingResponse remotingResponse = new RemotingResponse(request.getOpaque(), timeoutMillis);
            //将请求放入一个"篮子"中，等远程端填充该篮子中嗷嗷待哺的每一个结果集
            NettyRemotingBase.responseTable.put(request.getOpaque(), remotingResponse);
            //发送请求
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        //如果发送对象成功，则设置成功
                        remotingResponse.setSendRequestOK(true);
                        return;
                    } else {
                        remotingResponse.setSendRequestOK(false);
                    }
                    //如果请求发送直接失败，则默认将其从responseTable这个篮子中移除
                    NettyRemotingBase.responseTable.remove(request.getOpaque());
                    //失败的异常信息
                    remotingResponse.setCause(future.cause());
                    //设置当前请求的返回主体返回体是null(请求失败的情况下，返回的结果肯定是null)
                    remotingResponse.putResponse(null);
                    logger.warn("use channel [{}] send msg [{}] failed and failed reason is [{}]", channel, request, future.cause().getMessage());
                }
            });

            RemotingTransporter remotingTransporter = remotingResponse.waitResponse();
            if (null == remotingTransporter) {
                //如果发送是成功的，则说明远程端，处理超时了
                if (remotingResponse.isSendRequestOK()) {
                    throw new RuntimeException("处理超时", remotingResponse.getCause());
                } else {
                    throw new RuntimeException("未发送成功",
                            remotingResponse.getCause());
                }
            }
            return remotingTransporter;
        } finally {
            //最后不管怎么样，都需要将其从篮子中移除出来，否则篮子会撑爆的
            NettyRemotingBase.responseTable.remove(request.getOpaque());
        }
    }
}

package com.wm.IO.Netty.highDemo;

import com.wm.IO.Netty.highDemo.modal.RemotingResponse;
import com.wm.IO.Netty.highDemo.modal.RemotingTransporter;
import com.wm.IO.Netty.highDemo.protocal.LaopopoProtocol;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wm.IO.Netty.highDemo.protocal.LaopopoProtocol.REQUEST_REMOTING;
import static com.wm.IO.Netty.highDemo.protocal.LaopopoProtocol.RESPONSE_REMOTING;
import static com.wm.IO.Netty.highDemo.serialization.SerializerHolder.serializerImpl;

/**
 * @Auther: miaomiao
 * @Date: 18/6/7 15:45
 * @Description:
 */
public class NettyRemotingBase {
    private static final Logger logger = LoggerFactory.getLogger(NettyRemotingBase.class);

    /******key为请求的opaque value是远程返回的结果封装类******/
    public static final ConcurrentHashMap<Long, RemotingResponse> responseTable = new ConcurrentHashMap<Long, RemotingResponse>(256);



    protected void processMessageReceived(ChannelHandlerContext ctx, RemotingTransporter msg) {

        if (logger.isDebugEnabled()) {
            logger.debug("channel [] received RemotingTransporter is [{}]", ctx.channel(), msg);
        }

        final RemotingTransporter remotingTransporter = msg;

        System.out.println(new String(remotingTransporter.bytes()));

        if (remotingTransporter != null) {
            switch (remotingTransporter.getTransporterType()) {
                //作为server端 client端的请求的对应的处理
                case REQUEST_REMOTING:
                    processRemotingRequest(ctx, remotingTransporter);
                    break;
                //作为客户端，来自server端的响应的处理
                case RESPONSE_REMOTING:
                    processRemotingResponse(ctx, remotingTransporter);
                    break;
                default:
                    break;
            }
        }
    }

    protected void processRemotingRequest(final ChannelHandlerContext ctx, final RemotingTransporter remotingTransporter) {


            Runnable run = new Runnable() {

                @Override
                public void run() {
                    try {
                        final RemotingTransporter response =  processRequest(ctx, remotingTransporter);

                        //调用后，做一些文章
                        if (null != response) {
                            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {

                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    if (!future.isSuccess()) {
                                        logger.error("fail send response ,exception is [{}]", future.cause().getMessage());
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        logger.error("processor occur exception [{}]", e.getMessage());
                        final RemotingTransporter response = RemotingTransporter.newInstance(remotingTransporter.getOpaque(), LaopopoProtocol.RESPONSE_REMOTING, LaopopoProtocol.HANDLER_ERROR, null);
                        ctx.writeAndFlush(response);
                    }
                }
            };
            try {
                Executors.newCachedThreadPool().submit(run);
            } catch (Exception e) {
                logger.error("server is busy,[{}]", e.getMessage());
                final RemotingTransporter response = RemotingTransporter.newInstance(remotingTransporter.getOpaque(), LaopopoProtocol.RESPONSE_REMOTING, LaopopoProtocol.HANDLER_BUSY, null);
                ctx.writeAndFlush(response);
            }
    }


    /**
     * client处理server端返回的消息的处理
     *
     * @param ctx
     * @param remotingTransporter
     */
    protected void processRemotingResponse(ChannelHandlerContext ctx, RemotingTransporter remotingTransporter) {
        //从缓存篮子里拿出对应请求的对应响应的载体RemotingResponse
        final RemotingResponse remotingResponse = responseTable.get(remotingTransporter.getOpaque());
        //不超时的情况下
        if (null != remotingResponse) {
            //首先先设值，这样会在countdownlatch wait之前把值赋上
            remotingResponse.setRemotingTransporter(remotingTransporter);
            //可以直接countdown
            remotingResponse.putResponse(remotingTransporter);
            //从篮子中移除
            responseTable.remove(remotingTransporter.getOpaque());
        } else {
            logger.warn("received response but matched Id is removed from responseTable maybe timeout");
            logger.warn(remotingTransporter.toString());
        }
    }


    public RemotingTransporter processRequest(ChannelHandlerContext ctx, RemotingTransporter transporter) throws Exception {
        transporter.setCustomHeader(serializerImpl().readObject(transporter.bytes(), TestCommonCustomBody.class));
        System.out.println(transporter);
        transporter.setTransporterType(LaopopoProtocol.RESPONSE_REMOTING);
        return transporter;
    }
}

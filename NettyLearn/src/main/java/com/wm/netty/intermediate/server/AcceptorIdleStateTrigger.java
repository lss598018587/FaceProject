package com.wm.netty.intermediate.server;

/**
 * @Auther: miaomiao
 * @Date: 18/6/21 14:34
 * @Description:
 *
 * 单独写一个AcceptorIdleStateTrigger，
 * 其实也是继承ChannelInboundHandlerAdapter，
 * 重写userEventTriggered方法，
 * 因为客户端是write，那么服务端自然是read，设置的状态就是IdleState.READER_IDLE，
 *
 * 使用 Netty 实现心跳机制的关键就是利用 IdleStateHandler 来产生对应的 idle 事件.
 */
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


@ChannelHandler.Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter{
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {   //长时间没有处理客户端的信息
                System.out.println("长时间没有处理客户端的信息");
                throw new Exception("idle exception");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}

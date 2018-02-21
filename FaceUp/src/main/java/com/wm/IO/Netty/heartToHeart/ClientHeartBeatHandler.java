package com.wm.IO.Netty.heartToHeart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class ClientHeartBeatHandler extends ChannelHandlerAdapter {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> heartBeat;

    private InetAddress addr;

    private static final String SUCCESS_KEY = "auth_success_key";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        String key = "1234";
        String auth = ip+","+key;
        ctx.writeAndFlush(auth);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if(msg instanceof String){
                String ret = (String)msg;
                if(SUCCESS_KEY.equals(ret)){
                    /**
                     * 握手成功，主动发送心跳信息
                     * 第一个参数代表执行当 run方法
                     * 第二个参数表示延迟多少秒开始执行
                     * 第三个参数表示，循环多少秒执行run方法
                     * 第三个参数表示，时间单位
                     */
                    this.heartBeat = this.scheduler.scheduleWithFixedDelay(new HeartBeatTask(ctx),1,4, TimeUnit.SECONDS);
                    System.out.println(msg);
                }else {
                    System.out.println(msg);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(heartBeat!=null){
            heartBeat.cancel(true);
            heartBeat=null;
        }
        ctx.fireExceptionCaught(cause);
    }


    private class HeartBeatTask implements Runnable{
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try{
                RequestInfo info = new RequestInfo();
                info.setIp(addr.getHostAddress());
                Sigar sigar = new Sigar();
                CpuPerc cpuPerc = sigar.getCpuPerc();
                HashMap<String,String> cpu = new HashMap<>();
                cpu.put("sys",cpuPerc.getSys()+"");
                info.setCpuPerc(cpu);

                HashMap<String,String> memory = new HashMap<>();
                Mem mem = sigar.getMem();
                memory.put("total",mem.getTotal()+"");
                info.setMemory(memory);

                ctx.writeAndFlush(info);

            } catch (SigarException e) {
                e.printStackTrace();
            }
        }
    }
}

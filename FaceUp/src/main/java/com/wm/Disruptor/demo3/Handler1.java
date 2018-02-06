package com.wm.Disruptor.demo3;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.wm.Disruptor.demo2.Trade;

/**
 * @Author:wangmiao
 * @Desc 休眠2S和休眠2s以上结果不一样
 * @Date Created in   2018/1/25 .
 */
public class Handler1 implements EventHandler<Trade> {
    @Override
    public void onEvent(Trade trade, long l, boolean b) throws Exception {
        System.out.println("Handler1:"+trade.getId()+"-----"+trade.getPrice());
//        System.out.println("Handler1:"+trade);
        Thread.sleep(2000);
    }

}

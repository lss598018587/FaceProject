package com.wm.Disruptor.demo3;

import com.lmax.disruptor.EventHandler;
import com.wm.Disruptor.demo2.Trade;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/25 .
 */
public class Handler3 implements EventHandler<Trade>  {
    @Override
    public void onEvent(Trade trade, long l, boolean b) throws Exception {
        System.out.println("Handler3:"+trade.getId()+"-----"+trade.getPrice()+"------");
//        System.out.println("Handler3:"+trade);
    }

}

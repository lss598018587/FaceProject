package com.wm.Disruptor.demo2;

import com.lmax.disruptor.EventFactory;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/25 .
 */
public class TradeFactory implements EventFactory<Trade> {
    @Override
    public Trade newInstance() {
        return new Trade();
    }
}

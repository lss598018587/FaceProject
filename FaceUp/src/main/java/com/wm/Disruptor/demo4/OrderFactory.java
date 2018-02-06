package com.wm.Disruptor.demo4;

import com.lmax.disruptor.EventFactory;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/26 .
 */
public class OrderFactory implements EventFactory<Order> {
    @Override
    public Order newInstance() {
        return new Order();
    }
}

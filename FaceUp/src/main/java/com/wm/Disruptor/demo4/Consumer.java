package com.wm.Disruptor.demo4;

import com.lmax.disruptor.WorkHandler;
import javafx.animation.Transition;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/26 .
 */
public class Consumer implements WorkHandler<Order> {
    private String consumerId;

    private static AtomicInteger count = new AtomicInteger(0);

    public Consumer(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public void onEvent(Order order) throws Exception {
        System.out.println("当前消费者:"+consumerId+" ,Order信息："+order.getId()+"---------"+order.getPrice());
        count.incrementAndGet();
    }
    public int getCount(){
        return count.get();
    }
}

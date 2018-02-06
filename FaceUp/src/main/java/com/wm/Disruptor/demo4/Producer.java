package com.wm.Disruptor.demo4;

import com.lmax.disruptor.RingBuffer;

import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/26 .
 */
public class Producer {
    private final RingBuffer<Order> ringBuffer;
    private Random random = new Random();
    public Producer(RingBuffer<Order> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
    public void onData(String data){
        long sequene = ringBuffer.next();
        try {
            Order order = ringBuffer.get(sequene);
            order.setId(data);
            order.setPrice(random.nextInt(100)*999);
        }finally {
            ringBuffer.publish(sequene);
        }
    }
}

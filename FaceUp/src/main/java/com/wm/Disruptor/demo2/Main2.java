package com.wm.Disruptor.demo2;

import com.lmax.disruptor.*;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/25 .
 */
public class Main2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int BUFFER_SIZE = 1024;
        int THREAD_NUMBER=4;
        EventFactory eventFactory = new TradeFactory();
        /**
         * createSingleProducer创建一个单生产者的RingBuffer
         * 第一个参数 EventFactory,从名字上理解就是“事件工厂”，其实它的职责就是产生数据填充RingBuffer的区块
         * 第二个参数Ringbuffer的大小，它必须是2的N次方，目的是为了将求模运算转为&运算提高效率
         * 第三参数 RingBuffer的生产都在没有可用区块的时候（可能是消费者（或者说是时间处理器）太慢了）的等待策略
         */
         RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(eventFactory,BUFFER_SIZE,new YieldingWaitStrategy());

        //创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        WorkHandler<Trade> handler = new TradeHandler();

        WorkerPool<Trade> workerPool = new WorkerPool<Trade>(ringBuffer,sequenceBarrier,new IgnoreExceptionHandler(),handler);

        workerPool.start(executor);

        //下面这个生产8个数据
        for(int i=0;i<8;i++){
            long seq = ringBuffer.next();
            Trade trade = ringBuffer.get(seq); //给这个区块放入数据
            trade.setPrice(Math.random()*9999);
            trade.setId(UUID.randomUUID().toString());
            ringBuffer.publish(seq); //发布这个区块的数据使handler（consumer）可见
        }

        System.out.println("生产者结束了");
        Thread.sleep(1000); //等待消息处理器处理完
        workerPool.halt(); // 通知时间（或者说消息） 处理器可以结束了（并不是马上结束！！！）
        executor.shutdown();    //终止线程
    }
}

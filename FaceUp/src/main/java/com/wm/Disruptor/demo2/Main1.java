package com.wm.Disruptor.demo2;

import com.lmax.disruptor.*;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/25 .
 */
public class Main1 {
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

        //创建消息处理器
         BatchEventProcessor<Trade> transProcessor = new BatchEventProcessor<Trade>( ringBuffer, sequenceBarrier, new TradeHandler());

         //这一步的目的就是把消费者的位置信息引用注入到生产者  如果只有一个消费者的情况可以省略
        ringBuffer.addGatingSequences(transProcessor.getSequence());

        //把消息处理器提交到线程池
        executor.submit(transProcessor);

        //如果存在多个消费者 那重复执行上面3行代码，吧TradeHandler换成其他消费者类
        Future<?> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                long seq;
                for (int i = 0; i <10 ; i++) {
                    seq = ringBuffer.next(); //占个坑---ringBuffer一个可用区块
                    Trade trade = ringBuffer.get(seq); //给这个区块放入数据
                    trade.setPrice(Math.random()*9999);
                    trade.setId(UUID.randomUUID().toString());
                    ringBuffer.publish(seq); //发布这个区块的数据使handler（consumer）可见
                }
                return null;
            }
        });
        future.get(); //等待生产者结束
        System.out.println("生产者结束了");
        Thread.sleep(1000); //等待消息处理器处理完
        transProcessor.halt(); // 通知时间（或者说消息） 处理器可以结束了（并不是马上结束！！！）
        executor.shutdown();    //终止线程
    }
}

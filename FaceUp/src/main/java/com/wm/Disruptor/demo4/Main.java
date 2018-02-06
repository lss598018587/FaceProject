package com.wm.Disruptor.demo4;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/26 .
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        int bufferSize = 1024;
        int THREAD_NUM = Runtime.getRuntime().availableProcessors();
        System.out.println("THREAD_NUM>>" + THREAD_NUM);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);
        RingBuffer<Order> ringBuffer = RingBuffer.create(ProducerType.MULTI, new OrderFactory(), bufferSize, new YieldingWaitStrategy());
        SequenceBarrier barrier = ringBuffer.newBarrier();
        Consumer[] consumers = new Consumer[3];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer("c" + i);
        }
        WorkerPool<Order> workerPool = new WorkerPool<Order>(ringBuffer, barrier, new IntEventExceptionHandler(), consumers);
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(executor);

        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            final Producer p = new Producer(ringBuffer);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < 100; j++) {
                        p.onData("lyj" + j);
                    }
                }
            }).start();
        }
        Thread.sleep(2000);
        System.out.println("开始生产-------------------");
        latch.countDown();
        Thread.sleep(5000);
        System.out.println("总数：" + consumers[0].getCount());
    }
}

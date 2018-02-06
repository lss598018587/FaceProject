package com.wm.Executor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangmiao on 2018/1/20.
 */
public class UseThreadPoolExecutor2 implements Runnable{
    private static AtomicInteger count = new AtomicInteger(0);


    public static void main(String[] args) throws InterruptedException {
        //.
        //2.当线程池达到corePoolSize时，新提交任务将被放入workQueue中，等待线程池中任务调度执行
        //3.当workQueue已满，且maximumPoolSize>corePoolSize时，新提交任务会创建新线程执行任务
        //4.当提交任务数超过maximumPoolSize时，新提交任务由RejectedExecutionHandler处理
        /**
         * 无界队列
         * 与有界队列相比，除非资源耗尽，不然队列一直增加
         * 当线程池小于corePoolSize时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程。
         * 当达到corePoolSize后就不会增加，若后面仍有新的任务请求，而没有空闲的线程资源，则任务进入队列等待。
         * 若任务创建和处理的速度差异很大，无界队列会快速增长，直到资源耗尽。
         */
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        ExecutorService t = new ThreadPoolExecutor(
                5, // corePoolSize
                10,  //maximumPoolSize
                120, //
                TimeUnit.SECONDS,
                queue, //指定一种队列 workQueue
                new MyRejected()
        );

        for (int i = 0; i <20 ; i++) {
            t.execute(new UseThreadPoolExecutor2());
        }
        Thread.sleep(1000);
        System.out.println("队列长度"+queue.size());
        Thread.sleep(2000);
        t.shutdown();
    }

    @Override
    public void run() {
        try {
            int temp = count.incrementAndGet();
            System.out.println("任务 = " + temp);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

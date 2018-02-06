package com.wm.Design.Customer;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangmiao on 2018/1/19.
 */
public class Provider implements Runnable {

    //共享缓存区
    private BlockingQueue<Data> queue;
    //多线程间是否启动变量，有强制从主内存中刷新的功能，即时返回线程的状态
    private volatile boolean isRunning = true;
    //id生成器
    private static AtomicInteger count = new AtomicInteger();
    //随机对象
    private Random random = new Random();

    public Provider(BlockingQueue<Data> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                //随机休眠0-1000毫秒，表示获取数据时间
                Thread.sleep(random.nextInt(1000));
                //获取的数据进行累计
                int id = count.incrementAndGet();
                //比如遇到一个getData方法获取
                Data data = new Data(Integer.toString(id), "数据" + id);
                System.out.println("当前线程" + Thread.currentThread().getName() + "获取了数据，id为" + id);
                if (!this.queue.offer(data, 2, TimeUnit.SECONDS)) { //延迟2秒装到queue里，如果还是进不去就返回false
                    System.out.println("提交缓存区数据是吧");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.isRunning = false;
    }
}

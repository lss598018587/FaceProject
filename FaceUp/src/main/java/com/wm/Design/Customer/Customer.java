package com.wm.Design.Customer;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by wangmiao on 2018/1/19.
 */
public class Customer implements Runnable {
    //共享缓存区
    private BlockingQueue<Data> queue;

    public Customer(BlockingQueue<Data> queue) {
        this.queue = queue;
    }
    //随机对象
    private Random random = new Random();

    @Override
    public void run() {
while (true){
    try {
        Data data = this.queue.take(); //take是阻塞的去拿取
        Thread.sleep(random.nextInt(1000));
        System.out.println("当前线程"+Thread.currentThread().getName()+",成功消费 id为"+data.getId());
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
    }
}

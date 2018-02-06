package com.wm.Design.Customer;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Created by wangmiao on 2018/1/19.
 */
public class Main {
    public static void main(String[] args) {
        //内存缓冲区
        BlockingQueue<Data> queue = new LinkedBlockingQueue<Data>(10);
        Provider p1 =new Provider(queue);
        Provider p2 =new Provider(queue);
        Provider p3 =new Provider(queue);
        //消费者
        Customer c1 = new Customer(queue);
        Customer c2 = new Customer(queue);
        Customer c3 = new Customer(queue);

        //创建线程池运行，这是一个缓存的线程池，可以创建无穷大的线程，没有任务的时候不创建线程，空闲时间线程存活60s（默认值）
        ExecutorService cachePool = Executors.newCachedThreadPool();
        cachePool.execute(p1);
        cachePool.execute(p2);
        cachePool.execute(p3);
        cachePool.execute(c1);
        cachePool.execute(c2);
        cachePool.execute(c3);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        p1.stop();
        p2.stop();
        p3.stop();
    }
}

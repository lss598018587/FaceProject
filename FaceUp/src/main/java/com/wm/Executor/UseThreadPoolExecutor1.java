package com.wm.Executor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/1/20.
 */
public class UseThreadPoolExecutor1 {
    public static void main(String[] args) {
        /**
         * 有界队列
         * 1。当线程池小于corePoolSize时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程。
         * 2.当线程池达到corePoolSize时，新提交任务将被放入workQueue中，等待线程池中任务调度执行
         * 3.当workQueue已满，且maximumPoolSize>corePoolSize时，新提交任务会创建新线程执行任务
         * 4.当提交任务数超过maximumPoolSize时，新提交任务由RejectedExecutionHandler处理
         */
        ThreadPoolExecutor t = new ThreadPoolExecutor(
                1, // corePoolSize
                2,  //maximumPoolSize
                60, //
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(3), //指定一种队列 workQueue
                new MyRejected()
        );
        MyTask task1 =new MyTask(1,"任务1");
        MyTask task2 =new MyTask(2,"任务2");
        MyTask task3 =new MyTask(3,"任务3");
        MyTask task4 =new MyTask(4,"任务4");
        MyTask task5 =new MyTask(5,"任务5");
        MyTask task6 =new MyTask(6,"任务6");

        t.execute(task1);
        t.execute(task2);
        t.execute(task3);
        t.execute(task4);
        t.execute(task5);
        t.execute(task6);
        t.shutdown();
        while(true){
            if(t.isTerminated()){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh：mm：ss");
                System.out.println("整个结束时间"+format.format(new Date()));
                break;
            }
        }
    }
}

package com.wm.Thread.CyclicBarrier;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/23 .
 */
public class Client01 {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

        for (int i = 0; i < 5; i++) {
            new Worker(cyclicBarrier).start();
        }
        System.out.println("main 执行完毕");
    }

    static class Worker extends Thread{
        private CyclicBarrier cyclicBarrier;
        private Random random = new Random();

        public Worker(CyclicBarrier cyclicBarrier){
            cyclicBarrier = cyclicBarrier;
        }
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+"干活啦！！！");
            try {
                Thread.sleep(1000*random.nextInt(10));
                cyclicBarrier.await();
            System.out.println(Thread.currentThread().getName()+"结束！！干活啦！！！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }


    }
}

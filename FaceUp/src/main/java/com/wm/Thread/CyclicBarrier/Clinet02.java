package com.wm.Thread.CyclicBarrier;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/23 .
 */
public class Clinet02 {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2);
        for (int i = 0; i < 5; i++) {
            new Worker(semaphore).start();
        }
    }

    static class Worker extends Thread {
        private Semaphore semaphore;

        public Worker(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                //获取许可
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + "干活啦！！！");

                Thread.sleep(4000);
                semaphore.release();
                System.out.println(Thread.currentThread().getName() + "结束！！干活啦！！！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

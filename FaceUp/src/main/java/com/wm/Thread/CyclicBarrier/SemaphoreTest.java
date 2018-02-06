package com.wm.Thread.CyclicBarrier;


import java.util.concurrent.Semaphore;

/**
 * @Author:wangmiao
 *   Semaphore 和wait+notify有点像
 * 但Semaphore 一释放，就立刻让等待的线程继续运行
 * notify是要当前线程执行完毕才进行真正意义上的释放
 * @Date Created in   2018/1/22 .
 */
public class SemaphoreTest {
    public static void main(String[] args) {
        int n = 8; //工人数目
        Semaphore test = new Semaphore(5);//机器数目
        for (int i = 0; i <n ; i++) {
            new Worker(i,test).start();
        }

    }

    static class Worker extends Thread{
        private int num;
        private Semaphore semaphore;
        public Worker(int num,Semaphore semaphore){
            this.num =num;
            this.semaphore = semaphore;
        }

        @Override
        public void run(){
            try {
                //获取许可
                semaphore.acquire();
                System.out.printf("工人%s占用了一个，%s机器生产", this.num, Thread.currentThread().getName());
                System.out.println();
                Thread.sleep(2000);
                System.out.println("工人" + this.num + "释放了机器：" + Thread.currentThread().getName());
                //许可释放
                semaphore.release();
                System.out.println("工人" + this.num + "整理了一下机器：" + Thread.currentThread().getName());
                Thread.sleep(2000);
                System.out.println("工人" + this.num + "整理完毕机器：" + Thread.currentThread().getName());
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}

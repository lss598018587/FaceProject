package com.wm.Thread.scnzThis;

import java.util.concurrent.TimeUnit;

/**
 * @Author:wangmiao
 * @Desc 探究 notify 和wait
 *
 * 两个对象Thread4比如 t1 和 t2
 * t1停住 t2停住   t1线程另一个方法notify一下，t1 wait就会执行下去
 *
 *
 * @Date Created in   2018/1/19 .
 */
public class Thread4 {
    public void m4t1() {
        synchronized (this) {
            System.out.println(Thread.currentThread().getName() + " :  停住了");
            try {
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() +" :  停止结束要wait，等另一边notfy");
                wait();
                System.out.println(Thread.currentThread().getName() + ":  执行结束");
            } catch (InterruptedException ie) {
            }
        }
    }

    public synchronized void m4t2() {
        int i = 5;
            System.out.println(Thread.currentThread().getName() + " : 进来要释放锁了" );
            try {
                notify();
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }

    }
    public void m4t3() {
        synchronized (this) {
            System.out.println(Thread.currentThread().getName() + " :  停住了3333333333");
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() +" :  停止结束要wait，等另一边notfy3333333333333");
                wait();
                System.out.println(Thread.currentThread().getName() + ":  执行结束33333333333333333");
            } catch (InterruptedException ie) {
            }
        }
    }
    public static void main(String[] args) {

        final Thread4 myt2 = new Thread4();
        final Thread4 myt3 = new Thread4();
        Thread t1 = new Thread(
                new Runnable() {
                    public void run() {
                        myt2.m4t1();
                    }
                }, "t1"
        );
        Thread t3 = new Thread(
                new Runnable() {
                    public void run() {
                        myt3.m4t3();
                    }
                }, "t3"
        );

        Thread t2 = new Thread(
                new Runnable() {
                    public void run() {
                        myt2.m4t2();
                    }
                }, "t2"
        );
        t1.start();
        t3.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.start();

    }
}

package com.wm.Thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/17 .
 */
public class AtomicDemo {

    AtomicInteger ai=new AtomicInteger(0); //一致性

    volatile int mm =0;
    public void add() {
        for (int m = 0; m < 1000000; m++) {
            ai.getAndIncrement();
//            mm++;
        }
    }
    public static void main(String args[]) throws InterruptedException {
        final AtomicDemo mt = new AtomicDemo();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                mt.add();
            }
        },"t1");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                mt.add();
            }
        },"t2");
        t1.start();
        t2.start();
        Thread.sleep(500);
        System.out.println(mt.mm);
        System.out.println(mt.ai.get());
    }
}

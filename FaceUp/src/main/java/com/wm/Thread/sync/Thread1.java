package com.wm.Thread.sync;

/**
 * Created by wangmiao on 2018/1/21.
 * synchronized的重入
 */
public class Thread1 {
    public synchronized void method1() throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+"-----method1");
        Thread.sleep(500);
        method2();
    }
    public synchronized void method2() throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+"-----method2");
        Thread.sleep(500);
        method3();
    }
    public synchronized void method3(){
        System.out.println(Thread.currentThread().getName()+"-----method3");
    }

    public static void main(String[] args) {
        Thread1 t1 = new Thread1();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    t1.method1();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t111");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    t1.method1();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t222");
        t.start();
        t2.start();
    }
}

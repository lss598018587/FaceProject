package com.wm.Thread.scnzThis;

/**
 * Created by wangmiao on 2018/1/16.
 * 1.一个方法加synchronized，另个方法不加synchronized，一起同步访问
 * 2.另一个方法上加synchronized，不同步访问，先后访问
 * 3。另一个方法里加synchronized（this），不同步访问，先后访问
 */
public class Thread2 {
    public void m4t1() {

        synchronized (this) {
            int i = 5;
            while (i-- > 0) {
                System.out.println(Thread.currentThread().getName() + " : " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }
        }
    }
//
//    public  void m4t2() {
//        synchronized(this) {
//            int i = 5;
//            while (i-- > 0) {
//                System.out.println(Thread.currentThread().getName() + " : " + i);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException ie) {
//                }
//            }
//        }
//    }
//    public  void m4t2() {
//            int i = 5;
//            while (i-- > 0) {
//                System.out.println(Thread.currentThread().getName() + " : " + i);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException ie) {
//                }
//            }
//    }
    public synchronized void m4t2() {
        int i = 5;
        while (i-- > 0) {
            System.out.println(Thread.currentThread().getName() + " : " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
        }
    }
    public static void main(String[] args) {

        final Thread2 myt2 = new Thread2();
        final Thread2 myt1 = new Thread2();
        Thread t1 = new Thread(
                new Runnable() {
                    public void run() {
                        myt2.m4t1();
                    }
                }, "t1"
        );

        Thread t2 = new Thread(
                new Runnable() {
                    public void run() {
                        myt1.m4t2();
                    }
                }, "t2"
        );
        t1.start();
        t2.start();

    }
}
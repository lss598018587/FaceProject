package com.wm.Thread.ThreadToThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/17 .
 * <p>
 * wait方法释放锁，（notify唤醒wait的线程，但不释放锁，得等该线程执行完毕之后，wait的线程才能开始跑 ）
 */
public class Demo02 {
    private volatile static List list = new ArrayList();

    public void add() {
        list.add("wm zhen shuai");
    }

    public int size() {
        return list.size();
    }
    //要等t1完全跑完t2才能唤醒
    public static void main(String args[]) {
        final Demo02 demo = new Demo02();
        final Object lock = new Object();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        for (int i = 0; i < 10; i++) {
                            demo.add();
                            System.out.println("当前线程：" + Thread.currentThread().getName() + "添加了一个元素");
                            Thread.sleep(500);
                            if (demo.size() == 5) {
                                System.out.println("已经发出通知....");
                                lock.notify();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "t1");

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (demo.size() != 5) {
                            System.out.println("t2进入");
                            lock.wait();
                            System.out.println(" countDownLatch结束");
                        }
                        System.out.println("当前线程：" + Thread.currentThread().getName() + "收到通知线程停止");
                        throw new RuntimeException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "t2");
        t2.start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.start();
    }

    //222222222222222222222222222222 加里面很尴尬，虽然通知了，但是之后循环又占用了锁，t2和t1还是要竞争锁
//    public static void main(String args[]) {
//        final Demo02 demo = new Demo02();
//        final Object lock = new Object();
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    for (int i = 0; i < 10; i++) {
//                        synchronized (lock) {
//                            demo.add();
//                            System.out.println("当前线程：" + Thread.currentThread().getName() + "添加了一个元素");
//                            Thread.sleep(500);
//                            if (demo.size() == 5) {
//                                System.out.println("已经发出通知....");
//                                lock.notify();
//                            }
//                        }
////                        Thread.sleep(1500);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, "t1");
//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (lock) {
//                        if (demo.size() != 5) {
//                            System.out.println("t2进入");
//                            lock.wait();
//                            System.out.println(" countDownLatch结束");
//                        }
//                        System.out.println("当前线程：" + Thread.currentThread().getName() + "收到通知线程停止");
//                        throw new RuntimeException();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, "t2");
//        t2.start();
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        t1.start();
//    }
    //33333333333333333333333333333333333 及时发出通信的方法countDownLatch
//    public static void main(String args[]) {
//        final Demo02 demo = new Demo02();
//        final CountDownLatch countDownLatch = new CountDownLatch(1);
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    for (int i = 0; i < 10; i++) {
//                        demo.add();
//                        System.out.println("当前线程：" + Thread.currentThread().getName() + "添加了一个元素");
//                        Thread.sleep(500);
//                        if (demo.size() == 5) {
//                            System.out.println("已经发出通知....");
//                            countDownLatch.countDown();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, "t1");
//
//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (demo.size() != 5) {
//                        System.out.println("t2进入");
//                        countDownLatch.await();
//                        System.out.println(" countDownLatch结束");
//                    }
//                    System.out.println("当前线程：" + Thread.currentThread().getName() + "收到通知线程停止");
//                    throw new RuntimeException();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, "t2");
//        t2.start();
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        t1.start();
//    }
}

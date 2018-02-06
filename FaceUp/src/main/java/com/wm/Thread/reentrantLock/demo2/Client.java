package com.wm.Thread.reentrantLock.demo2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author:wangmiao
 * @Desc  condition  await 和 signal 的形式和  wait notify一模一样
 * signal和notify是一样的，等线程跑完，才真正的去发通知
 * @Date Created in   2018/1/23 .
 */
public class Client {
    public static void main(String[] args) {
        Link link = new Link();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                link.put();
            }
        }, "t1");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                link.read();
            }
        }, "t2");
        t2.start();
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.start();


    }

    static class Link {
        private Lock lock = new ReentrantLock();
        private List<String> list = new ArrayList<String>();
        private Condition condition = lock.newCondition();

        public void read() {
            try {
                lock.lock();
                System.out.println("read 进来了");
                while (list.isEmpty()) {
                    condition.await();
                }
                System.out.println(list.remove(0));
                condition.signal();
                System.out.println("read >>> 已经发出，提取物品的信号了");
                Thread.sleep(3000);
                System.out.println("read >>> 等待三秒了，执行结束了");
            } catch (Exception e) {

            }finally {
                lock.unlock();
            }
        }

        public void put() {
            try {
                lock.lock();
                System.out.println("put 进来了");
                while (list.size() == 5) {
                    condition.await();
                }
                list.add("zaoan");
                condition.signal();
                System.out.println("put >>> 已经发出，存物品的信号了");
                Thread.sleep(3000);
                System.out.println("put >>> 等待三秒了，执行结束了");
            } catch (Exception e) {

            }finally {
                lock.unlock();
            }
        }
    }
}

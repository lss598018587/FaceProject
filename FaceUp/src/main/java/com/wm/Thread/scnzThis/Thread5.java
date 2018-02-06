package com.wm.Thread.scnzThis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author:wangmiao
 * @Desc 探究 notify 和wait
 *
 *  wait会释放锁！！！！
 *
 *
 * @Date Created in   2018/1/19 .
 */
public class Thread5 {
//    public List<String> list = Collections.synchronizedList(new ArrayList());
    public List<String> list =  new ArrayList();
    public synchronized void put(String str) throws InterruptedException {
            System.out.println("put"+Thread.currentThread().getName()+"进入方法里面");
        while (getSize()==1){
            System.out.println("put"+Thread.currentThread().getName()+"进来等待");
            wait();
        }
        Thread.sleep(3000);
        list.add(str);
        notify();
        System.out.println("put"+Thread.currentThread().getName()+"插入成功");
    }
    public int getSize(){
        return list.size();
    }
    public synchronized void get() throws InterruptedException {
            System.out.println("get"+Thread.currentThread().getName()+"进入方法里面");
        while (list.size() == 0){
            System.out.println("get"+Thread.currentThread().getName()+"进来等待");
            wait();
        }
        String str = list.remove(0);
        System.out.println("get"+Thread.currentThread().getName()+"取出了数据："+str);
        notify();
    }
    public static void main(String[] args) {

        final Thread5 t11 = new Thread5();

            Thread t1 = new Thread(
                    new Runnable() {
                        public void run() {
                            try {
                                t11.put("12");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }, "t1"
            );t1.start();
            Thread t2 = new Thread(
                    new Runnable() {
                        public void run() {
                            try {
                                t11.put("12");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }, "t2"
            );t2.start();
        Thread t5 = new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            t11.put("12");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "t5"
        );t5.start();
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            Thread t3 = new Thread(
                    new Runnable() {
                        public void run() {
                            try {
                                t11.get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }, "tttt3"
            );
            t3.start();

        Thread t4 = new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            t11.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "tttt4"
        );
        t4.start();
        Thread t6 = new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            t11.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "tttt6"
        );
        t6.start();

    }
}

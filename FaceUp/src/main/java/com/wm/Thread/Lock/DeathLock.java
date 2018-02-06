package com.wm.Thread.Lock;

import java.util.concurrent.TimeUnit;

/**
 * @Author:wangmiao
 * @Desc 死锁问题，在设计程序时就应该避免双方互相持有对方的锁的情况
 * @Date Created in   2018/1/18 .
 */
public class DeathLock implements Runnable{

    private String tag;
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();

    public void setTag(String tag){
        this.tag = tag;
    }

    @Override
    public void run() {
        if(tag.equals("a")){
            synchronized (lock1){
                try {
                    System.out.println("当前线程："+Thread.currentThread().getName()+"进入locak1执行");
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                synchronized (lock2){
                    System.out.println("当前线程："+Thread.currentThread().getName()+"进入locak2执行");
                }
            }
        }
        if(tag.equals("b")){
            synchronized (lock2){
                try {
                    System.out.println("当前线程："+Thread.currentThread().getName()+"进入locak2执行");
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                synchronized (lock1){
                    System.out.println("当前线程："+Thread.currentThread().getName()+"进入locak1执行");
                }
            }
        }
    }


    public static void main(String args[]){
          DeathLock d1 = new DeathLock();
          d1.setTag("a");
          DeathLock d2 = new DeathLock();
          d2.setTag("b");
        Thread t1 = new Thread(d1,"t1");
        Thread t2 = new Thread(d2,"t2");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.start();

    }
}

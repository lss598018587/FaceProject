package com.wm.Thread.Lock;

/**
 * @Author:wangmiao
 * @Desc 锁对象的改变问题
 * @Date Created in   2018/1/18 .
 */
public class ObjectLock {


    private void method01(){
        synchronized (this){   //对象锁
            try{
                System.out.println("当前线程："+Thread.currentThread().getName()+"开始");
                Thread.sleep(2000);
                System.out.println("当前线程"+Thread.currentThread().getName()+"结束");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void method02(){
        synchronized (ObjectLock.class){  //类锁
            try{
                System.out.println("当前线程："+Thread.currentThread().getName()+"开始");
                Thread.sleep(2000);
                System.out.println("当前线程"+Thread.currentThread().getName()+"结束");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private Object lock = new Object();
    private void method03(){
        synchronized (lock){  //任何对象锁
            try{
                System.out.println("当前线程："+Thread.currentThread().getName()+"开始");
                Thread.sleep(2000);
                System.out.println("当前线程"+Thread.currentThread().getName()+"结束");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String args[]){
        final ObjectLock objectLock = new ObjectLock();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                objectLock.method01();
            }
        },"t1");
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                objectLock.method02();
            }
        },"t2");
        t2.start();
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                objectLock.method03();
            }
        },"t3");
        t3.start();

    }
}

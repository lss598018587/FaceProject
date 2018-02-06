package com.wm.Thread.Lock;

/**
 * @Author:wangmiao
 * @Desc 锁对象的改变问题
 * @Date Created in   2018/1/18 .
 */
public class ChangeLock {

    private String lock = "lock";

    private void method(){
        synchronized (lock){
            try{
                System.out.println("当前线程："+Thread.currentThread().getName()+"开始");
                lock = "change lock";
                Thread.sleep(2000);
                System.out.println("当前线程"+Thread.currentThread().getName()+"结束");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]){
        final  ChangeLock changeLock = new ChangeLock();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                changeLock.method();
            }
        },"t1");
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                changeLock.method();
            }
        },"t2");
        t2.start();

    }
}

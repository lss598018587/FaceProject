package com.wm.Queue;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author:wangmiao
 * @Desc 同步进去，同步出来。不停留
 * @Date Created in   2018/1/18 .
 */
public class SynchronQueue {

    public static void main(String args[]){
        final SynchronousQueue<String >q = new SynchronousQueue<>();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("进入到取数据堵塞地方");
                    System.out.println(q.take());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },"t1");

        t1.start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                     q.add("wm shuai");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },"t1");
        t2.start();
    }
}

package com.wm.Queue;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangmiao on 2018/1/17.
 */
public class MyQueue {
    //需要一个承装元素的集合
    private LinkedList<Object> list = new LinkedList<>();

    //计数器
    private AtomicInteger count = new AtomicInteger(0);

    private final int minSize = 0;
    private final int maxSize;

    public MyQueue(int size) {
        this.maxSize = size;
    }

    //初始化一个对象用户加锁
    private final Object lock = new Object();

    //把Object加到lockingQueue里，如果BlockQueue里没有空间，则调用此方法的线程被阻断，直到BlockQueue里有空间再继续添加
    public void put(Object obj){
        synchronized (lock){
            while (count.get() == this.maxSize){
                try {
                    lock.wait();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //加入元素
            list.add(obj);
            //计数器增加
            count.incrementAndGet();
            //通知另一个线程（唤醒）
            lock.notify();
            System.out.println("新加入的元素"+obj);
        }
    }

    //take取走BlockQueue里排在首位的对象，若BlockQueue为空，阻断进入等待状态，直到BlockQueue有新的元素加入
    public Object take(){
        Object obj =null;
        synchronized (lock){
            while (count.get() == this.minSize){
                try {
                    lock.wait();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //移除元素操作
            obj = list.removeFirst();
            //计数器递减
            count.decrementAndGet();
            //通知另一个线程（唤醒）
            lock.notify();
        }
        return obj;
    }
    public int getSize(){
        return this.count.get();
    }

    public static void main(String args []){
        final  MyQueue myQueue = new MyQueue(5);
        myQueue.put("a");
        myQueue.put("b");
        myQueue.put("c");
        myQueue.put("d");
        myQueue.put("e");
        System.out.println("当前长度为："+myQueue.getSize());

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                myQueue.put("f");
                myQueue.put("g");
            }
        },"t1");

        t1.start();

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Object obj1 = myQueue.take();
                System.out.println("移除的元素为"+obj1);
                Object obj2 = myQueue.take();
                System.out.println("移除的元素为"+obj2);
            }
        },"t2");

        try {
            TimeUnit.SECONDS.sleep(2);//Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t2.start();
    }

}

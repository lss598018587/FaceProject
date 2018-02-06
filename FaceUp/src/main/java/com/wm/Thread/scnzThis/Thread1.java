package com.wm.Thread.scnzThis;

/**
 * Created by wangmiao on 2018/1/16.
 * synchronized (this) 在一个方法里，锁住的是一个对象对这个方法里的访问
 * 但如果有两个对象a1,a2。他们各自执行这个方法不会对互相产生影响
 */
public class Thread1 implements Runnable {
    @Override
    public void run() {
        synchronized (this) {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + " synchronized loop " + i);
            }
        }
    }
    public static void main(String[] args) {
        Thread1 t1 = new Thread1();
//        Thread1 t2 = new Thread1();
        Thread ta = new Thread(t1, "A");
        Thread tb = new Thread(t1, "B");
        ta.start();
        tb.start();
    }
}
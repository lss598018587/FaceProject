package com.wm.Base;

/**
 * Created by wangmiao on 2018/3/8.
 */
public  class SyncTest {
    static Object ob = new Object();
    public synchronized  void test01(){
//        synchronized (this){
            System.out.println("进来了");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("出去了");
//        }

    }
    public   void test02(){
//        synchronized (this){
            System.out.println("进来了");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("出去了");
//        }
    }
    public static void main(String[] args) {
        SyncTest st = new  SyncTest();
        SyncTest st2 = new  SyncTest();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                st.test01();
            }
        },"t1");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                st.test02();
            }
        },"t2");
        t1.start();
        t2.start();
        Test t = new SyncTest.Test();
        t.sum();
    }

    static class Test{
        public void sum(){
            System.out.println(123);
        }
    }
}

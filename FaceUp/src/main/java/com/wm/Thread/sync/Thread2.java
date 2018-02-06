package com.wm.Thread.sync;

/**
 * Created by wangmiao on 2018/1/21.
 */
public class Thread2 {
    static class Main{
        public int i=10;
        public synchronized void operationSup(){
            try{
                i--;
                System.out.println(Thread.currentThread().getName()+"  Main print i = " + i);
    Thread.sleep(500);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class Sub extends Main{
        public synchronized void operationSub(){
            try{
                while (i>0){
                    i--;
                    System.out.println(Thread.currentThread().getName()+"  Sub print i = " + i);
                    Thread.sleep(500);
                    this.operationSup();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Sub sub = new Sub();
        Sub sub2 = new Sub();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                sub.operationSub();
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                sub2.operationSub();
            }
        });
        t1.start();
        t2.start();
    }
}

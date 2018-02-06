package com.wm.Thread.sync;

/**
 * Created by wangmiao on 2018/1/21.
 */
public class SyncException {
    public int i=0;
    public synchronized void operation(){
        while (i<20){
            try {
                i++;
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName()+"  i="+i);
                if(i==10){
                    Integer.parseInt("a");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("log info i :"+i);
            }
        }
    }

    public static void main(String[] args) {
        SyncException syncException =new SyncException();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                syncException.operation();
            }
        });
        t1.start();
    }
}

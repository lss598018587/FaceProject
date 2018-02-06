package com.wm.dirtyRead;

/**
 * Created by wangmiao on 2018/1/16.
 * 一个类里有两个方法
     void setValue(){}
     void getValue(){}
     如果两个方法前面都加了synchronized,一个线程会在一个执行完之后，才执行第二个
 */
public class ThreadMain {
    private String userName = "wangmiao";
    private String password = "123456";

    public synchronized void setValue(String name,String pwd){
        this.userName = name;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.password = pwd;
        System.out.println("setValue最终结果为："+this.userName+"---"+this.password);
    }
    public   void getValue(){
        System.out.println("getValue结果为："+this.userName+"---"+this.password);
    }

    public static void main(String args[]){
        final ThreadMain threadMain = new ThreadMain();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                threadMain.setValue("123","456");
            }
        });
        t1.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadMain.getValue();
    }
}

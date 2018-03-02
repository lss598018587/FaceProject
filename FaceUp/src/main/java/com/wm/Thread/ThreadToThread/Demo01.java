package com.wm.Thread.ThreadToThread;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @Author:wangmiao
 * @Desc   一些面试题
 * @Date Created in   2018/1/17 .
 *
 *
 * static 内存共享变量，一个线程开启的时候就去方法区把变量已经拷贝到自己的线程里
 * 但如果用System.out.println去打印，就是用jvm自己开的线程去方法区把这个变量再次拷贝一份到自己的本地
 *
 */
public class Demo01 {

//    private  static Vector list = new Vector();
    private  static List list = new ArrayList();

    private static  Integer number = 0;

    public void add(){
        list.add("wm zhen shuai");
    }
    public int size(){
        return list.size();
    }

    public static void main(String args[]){
        final Demo01 demo = new Demo01();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10;i++){
                    list.add("123");
                    System.out.println("当前线程："+Thread.currentThread().getName()+"添加了一个元素");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"t1");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
               while (true){
//                   System.out.println("当前线程："+Thread.currentThread().getName()+"看下static里的list长度"+list.size());
                   if(list.size()>=5){
                       System.out.println("当前线程："+Thread.currentThread().getName()+"已经溢出,数字为"+list.size());
                       throw new RuntimeException();
                   }
               }
            }
        },"t2");

//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for(int i=0;i<10;i++){
//                    number++;
//                    System.out.println("当前线程："+Thread.currentThread().getName()+"给number增加了"+number);
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        },"t1");
//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//
//                        System.out.println("当前线程："+Thread.currentThread().getName()+"打印了"+number);
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        },"t2");
        t1.start();
        t2.start();

    }
}

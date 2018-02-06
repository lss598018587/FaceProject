package com.wm.Thread.Lock;

import java.util.concurrent.TimeUnit;

/**
 * @Author:wangmiao
 * @Desc 同一个对象属性的修改不会产生（锁对象改变）影响
 * @Date Created in   2018/1/18 .
 */
public class ModifyLock {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private static synchronized void changeAttributte(ModifyLock modifyLock,String name,int age){
            try{
                System.out.println("当前线程："+Thread.currentThread().getName()+"开始");
                modifyLock.setName(name);
                modifyLock.setAge(age);
                System.out.println("当前线程："+Thread.currentThread().getName()+"修改对象内容为："+modifyLock.getName()+","+modifyLock.getAge());
                Thread.sleep(2000);
                System.out.println("当前线程"+Thread.currentThread().getName()+"结束");
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    public static void main(String args[]){
        final ModifyLock modifyLock = new ModifyLock();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                modifyLock.changeAttributte(modifyLock,"wm",18);
            }
        },"t1");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                modifyLock.changeAttributte(modifyLock,"lyj",38);
            }
        },"t2");
        t1.start();
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.start();

    }
}

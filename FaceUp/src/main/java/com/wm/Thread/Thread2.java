package com.wm;

/**
 * Created by wangmiao on 2017/12/15.
 */
public class Thread2{

    public static void main(String[] args) {
        Job job1 = new Job();
        Job job2 = new Job();
        T1 t1 = new T1(job1);
        T2 t2 = new T2(job2);

        t1.start();
        t2.start();
    }
}

class Job{

    public void doTest(){
        synchronized(Job.class){
//        synchronized (this){
            try {
                String name = Thread.currentThread().getName();
                System.out.println("name = "+name);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class T1 extends Thread{
    private Job job;

    public T1(Job job){
        super("A");
        this.job = job;
    }

    @Override
    public void run() {
        job.doTest();
    }
}

class T2 extends Thread{
    private Job job;

    public T2(Job job){
        super("B");
        this.job = job;
    }

    @Override
    public void run() {
        this.job.doTest();
    }
}
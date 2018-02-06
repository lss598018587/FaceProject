package com.wm.Queue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/1/18.
 */
public class UseDelayQueue implements Runnable {
    private DelayQueue<Wangmin> delayQueue = new DelayQueue<Wangmin>();

    public boolean yinye = true;

    public void shangji(String name,String id,int money){
        Wangmin wangmin = new Wangmin(name,id,1000*money+System.currentTimeMillis());
        System.out.printf("网民%s，身份证%s，交钱%s块 开始上机",name,id,money);
        System.out.println();
        this.delayQueue.add(wangmin);
    }

    public void xiaji(Wangmin man){
        System.out.printf("网民%s，身份证%s，结束下机",man.getName(),man.getId());
        System.out.println();
    }
    @Override
    public void run() {
        while (yinye){
            try {
                Wangmin man = delayQueue.take();
                xiaji(man);
            }catch (Exception e){

            }
        }
    }

    public static void main(String args[]){
        System.out.println("王八开始营业");
        UseDelayQueue xulei = new UseDelayQueue();
        Thread thread = new Thread(xulei);
        thread.start();
        xulei.shangji("小红","123",2);
        xulei.shangji("丢丢","789",10);
        xulei.shangji("君君","456",4);
    }
}
class Wangmin implements Delayed{
    private String name;
    private String id;
    private long endTime;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public Wangmin(String name, String id, long endTime) {
        this.name = name;
        this.id = id;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return endTime-System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        Wangmin w = (Wangmin)o;

        return this.getDelay(this.timeUnit) - w.getDelay(this.timeUnit)>0?1:0;
    }
}

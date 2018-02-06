package com.wm.Executor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangmiao on 2018/1/20.
 */
public class MyTask implements Runnable{
    private int id;
    private String name;

    public MyTask(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        try {
            System.out.println("run taskId = "+this.toString());
            Thread.sleep(5000);
            System.out.println("run taskId = "+this.toString()+"执行完毕");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            System.out.println(this.toString()+"结束时间"+format.format(new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "MyTask{" +
                "id=" + id +
                '}';
    }
}

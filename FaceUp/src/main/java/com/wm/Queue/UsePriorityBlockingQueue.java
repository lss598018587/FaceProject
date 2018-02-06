package com.wm.Queue;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Author:wangmiao
 * @Desc  比较优先级的信息  队列
 * @Date Created in   2018/1/18 .
 */
public class UsePriorityBlockingQueue {

    public static void main(String args[]) throws InterruptedException {
        PriorityBlockingQueue<Task> q = new PriorityBlockingQueue<Task>();
        Task t1 = new Task();
        t1.setId(3);
        t1.setName("id wei 3");
        Task t2 = new Task();
        t2.setId(4);
        t2.setName("id wei 4");
        Task t3 = new Task();
        t3.setId(1);
        t3.setName("id wei 1");
        Task t4 = new Task();
        t4.setId(8);
        t4.setName("id wei 8");
        q.add(t1);
        q.add(t4);
        q.add(t2);
        q.add(t3);
        System.out.println(q);
//        System.out.println(q.take().toString());
//        System.out.println(q.take().toString());
//        System.out.println(q.take().toString());
//        System.out.println(q.take().toString());
    }

}
class Task implements Comparable<Task>{
    private int id;
    private String name;

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
    public int compareTo(Task o) {
        return this.id>o.getId()?1:(this.id<o.getId()?-1:0);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}


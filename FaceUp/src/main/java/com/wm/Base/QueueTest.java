package com.wm.Base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class QueueTest {
    static DelayQueue<Student> students = new DelayQueue<>();
    public static void main(String[] args) throws ParseException {
        ArrayBlockingQueue queue = new ArrayBlockingQueue(5);
        System.out.println(queue.add(4));
        System.out.println(queue.add(3));
        System.out.println(queue.add(1));
        System.out.println(queue.add(2));
        System.out.println(queue.add(6));
//        System.out.println(queue.remove());
        System.out.println(queue.poll());
        System.out.println(queue.size());
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Student t = new Student(format.parse("2018-03-12 13:48:23"),"w1");
//        Student t1 = new Student(format.parse("2018-03-12 13:49:53"),"w2");
//        Student t2 = new Student(format.parse("2018-03-12 13:49:23"),"w3");
//        students.put(t);
//        students.put(t1);
//        System.out.println(students);
//        students.put(t2);
//        System.out.println(students);
//
//
//        Thread tt1 = new Thread(new MainRun());
//        tt1.start();
    }

    static class Student implements Delayed {
        private Date time;
        private String name;

        public Student(Date time, String name) {
            this.time = time;
            this.name = name;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long ti = this.time.getTime() - System.currentTimeMillis();
//            System.out.println(ti);
            return ti;
        }

        @Override
        public int compareTo(Delayed o) {
            Student t = (Student)o;
            return this.time.getTime()-t.getTime().getTime()>0?1:0;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "time=" + time +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    static class MainRun implements Runnable{

        @Override
        public void run() {
            for(int i=0;i<3;i++){
                try {
                    Student s  = students.take();
                    System.out.println(s.getName()+"chu qu le");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

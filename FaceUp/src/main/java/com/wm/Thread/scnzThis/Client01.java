package com.wm.Thread.scnzThis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/23 .
 */
public class Client01 {
    public static void main(String[] args) {
        Test t = new Test();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                t.run();
            }
        }, "t1");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                t.run();
            }
        }, "t2");
        t1.run();
        t2.run();
    }

    static class Test {
        private SimpleDateFormat format = new SimpleDateFormat("hh:mm:sss");

        private void run() {
            synchronized (this) {
                try {
                    System.out.println("开始：" + format.format(new Date()));
                    Thread.sleep(3000);
                    System.out.println("结束：" + format.format(new Date()));

                } catch (Exception e) {

                }
            }
        }
    }
}

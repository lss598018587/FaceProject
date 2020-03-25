package com.wm;

/**
 * @Auther: miaomiao
 * @Date: 2019-09-09 10:01
 * @Description:
 */
public class GoDo {
    public int m =5;

    public GoDo(int m) {
        this.m = m;
    }

    public  void sayHello() {
        try {
            Thread.sleep(2000);
            System.out.println("hello 3333333 world!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  void sayHello2(String hello) {
        try {
            Thread.sleep(1000);
            System.out.println(hello);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

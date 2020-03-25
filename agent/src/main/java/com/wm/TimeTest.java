package com.wm;

/**
 * @Auther: miaomiao
 * @Date: 2019-08-19 10:00
 * @Description:
 */
public class TimeTest {
    public static void main(String[] args) throws InterruptedException {
        GoDo a = new GoDo(2);
        a.sayHello();
        a.sayHello2("hello world222222222");
        while (true){
            Thread.sleep(10000);
            System.out.println("=============10s，调用一次============");
            new GoDo(2).sayHello();
        }
    }


}

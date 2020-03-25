package com.wm.attach4;

import com.wm.GoDo;

/**
 * @Auther: miaomiao
 * @Date: 2019-09-19 14:40
 * @Description:
 */
public class Queue {
    public static void main(String[] args) throws InterruptedException {
        GoDo tail = new GoDo(2);

        for (GoDo t = tail,p=t;;){
            System.out.println(t.m);
            System.out.println(p.m);
            Thread.sleep(3000);
            break;
        }
    }
}

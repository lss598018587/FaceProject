package com.wm.COW;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/22 .
 */
public class CowArrList {
    public static void main(String[] args) throws InterruptedException {
        List<String> a = new ArrayList<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        final CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>(a);
//        Thread t = new Thread(new Runnable() {
//            int count = -1;
//            @Override
//            public void run() {
//                while (count<10) {
//                    list.add(count++ + "");
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        t.setDaemon(true);
//        t.start();
//        Thread.currentThread().sleep(3000);
            System.out.println("原先的hash："+list.hashCode());
        for (String s : list) {
            System.out.println("copy之后的列表："+s);
        }
        list.remove("a");
            System.out.println("中间的hash："+list.hashCode());
        for (String s : list) {
            System.out.println(s);
        }
            System.out.println("之后的hash："+list.hashCode());
        for (String s : a) {
            System.out.println("原始列表："+s);
        }

    }
}

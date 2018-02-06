package com.wm.COW;

import java.io.IOException;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/22 .
 */
public class TestMain4 extends Thread {
    public void run() {            //永真循环线程
        for(int i=0;;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {   }
            System.out.println(i);
        }
    }

    public static void main(String [] args){
//        TestMain4 test = new TestMain4();
//        test.setDaemon(true);    //调试时可以设置为false，那么这个程序是个死循环，没有退出条件。设置为true，即可主线程结束，test线程也结束。
//        test.start();
//        System.out.println("isDaemon = " + test.isDaemon());
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        111010101101010111100000110
//                  11110001111100010

        String num ="01010001100000010";
        System.out.println(Long.parseLong(num,2));
        Long hash = 123121414L;
        Long zzz = 123874L;
        String m = Long.toBinaryString(hash);
        System.out.println(m);
        System.out.println(Long.toBinaryString(zzz));
        System.out.println(Long.parseLong(m,2));
        m= m.substring(0,m.length()-3);
        System.out.println(Long.parseLong(m,2));
        System.out.println(hash >>> 3);
        System.out.println(hash & zzz);
    }
}
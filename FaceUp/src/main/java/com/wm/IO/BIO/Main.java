package com.wm.IO.BIO;

import java.io.IOException;
import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/29 .
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerNomal.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(2000);
        Random random = new Random();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String msg = random.nextInt(10) +"：发送信息";
                    Client.send(msg);
                    try {
                        Thread.currentThread().sleep(random.nextInt(2000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

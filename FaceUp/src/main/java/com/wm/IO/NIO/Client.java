package com.wm.IO.NIO;

import java.util.Scanner;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/29 .
 */
public class Client {
    private final static String DEFAULT_IP = "127.0.0.1";
    private final static int port = 6789;
    private static ClinetHandle clinetHandle;

    public static void start(){
        if(clinetHandle!=null) clinetHandle.stop();
        clinetHandle = new ClinetHandle(DEFAULT_IP,port);
        new Thread(clinetHandle,"喵喵客户端").start();
    }

    //向服务器发送消息
    public static boolean sendMsg(String msg) throws Exception{
        if(msg.equals("q")) return false;
        clinetHandle.sendMsg(msg);
        return true;
    }

    public static void main(String[] args) throws Exception {
        start();
        while(Client.sendMsg(new Scanner(System.in).nextLine()));
    }
}

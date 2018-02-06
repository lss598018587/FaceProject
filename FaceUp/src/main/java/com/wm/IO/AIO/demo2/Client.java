package com.wm.IO.AIO.demo2;

import java.util.Scanner;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/5 .
 */
public class Client {
    private final static String ip = "127.0.0.1";
    private final static  int port = 6789;
    private  static AsyncClientHandler clientHandler;
    public static void start(){
        start(ip,port);
    }
    public static synchronized void start(String ip,int port){
        if(clientHandler!=null) return;
        clientHandler = new AsyncClientHandler(ip,port);
        new Thread(clientHandler,"喵喵客户端").start();
    }

    public static boolean sendMsg(String msg){
        if(msg.equals("q"))return false;
        clientHandler.sendMsg(msg);
        return true;
    }

    public static void main(String[] args) {
        start();
        System.out.println("请输入请求消息：");
        Scanner scanner = new Scanner(System.in);
        while (Client.sendMsg(scanner.nextLine()));
    }
}

package com.wm.IO.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/29 .
 */
public class Client {
    private final static int PORT = 8765;
    private final static String SERVER_IP="127.0.0.1";
    public static void send(String expression){
        send(PORT,expression);
    }
    private static void send(int port ,String expression){
        Socket socket =null;
        BufferedReader in = null;
        PrintWriter out = null;
        try{
            socket = new Socket(SERVER_IP,port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
            out.println(expression);
            out.println("123456789");
            System.out.println("结果为："+in.readLine());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                out.close();
            }
            if(socket !=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            socket=null;

        }
    }

    public static void main(String[] args) {
        Random random = new Random();
        String msg = random.nextInt(10) +"：发送信息";
        Client.send(msg);
        try {
            Thread.currentThread().sleep(random.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.wm.IO.BIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO服务端
 */
public class BioService1 {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(10101);
        System.out.println("服务器启动!");
        while (true){
            //获取一个套接字(阻塞)
            final Socket socket = serverSocket.accept();
            System.out.println("来了一个新客户端！");
            //业务处理
            handler(socket);
        }
    }

    /**
     * 读取数据
     * @param socket
     */
    private static void handler(Socket socket){
        try{
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream input = new ObjectInputStream(inputStream);
            String methodName = input.readUTF();
            System.out.println("methodName>>>"+methodName);
            String parameterTypes =  input.readObject().toString();
            System.out.println("parameterTypes》》"+parameterTypes);
            String arguments =  input.readObject().toString();
            System.out.println("arguments》》"+arguments);
//            InputStream inputStream = socket.getInputStream();
//            while (true){
//                //读取数据 阻塞
//                int read = inputStream.read(bytes);
//                if(read != -1){
//                    System.out.println(new String(bytes,0,read));
//                    System.out.println("已读一行");
//                }else{
//                    break;
//                }
//            }
            Thread.sleep(8000);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            try{
                System.out.println("socket关闭");
                socket.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
    }
}
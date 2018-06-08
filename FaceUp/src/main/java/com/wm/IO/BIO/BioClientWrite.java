package com.wm.IO.BIO;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class BioClientWrite {



    public static void main(String[] args) {

        Socket socket = null;
        BufferedReader in = null;
//        OutputStream os = null;
//        Scanner scan = new Scanner(System.in);
        ObjectOutputStream output=null;
        try {
            socket = new Socket("127.0.0.1", 10101);
            System.out.println(" client init ");
//                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            os = socket.getOutputStream();
//            while (true) {
//                String str = scan.nextLine();
//                os.write(str.getBytes());
//                os.flush();
//            }
//                        System.out.println(" client send ");
//                        System.out.println(" client received : " + in.readLine());
              output = new ObjectOutputStream(socket.getOutputStream());
            output.writeUTF("123123sawer");
            Thread.sleep(6000);
            output.writeObject("qweqw");//writeObject有flush的功能，而且还能序列号对象
            Thread.sleep(6000);
            output.writeUTF("1111111111111111");
            output.writeObject("345346");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

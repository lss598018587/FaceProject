package com.wm.IO.Netty.demo3;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by wangmiao on 2018/2/18.
 */
public class GzipUtils {
    public static byte[] gzip(byte[] data) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.finish();
        gzip.close();
        byte[] ret = bos.toByteArray();
        bos.close();
        return ret;
    }
    public static byte[] ungzip(byte[] data)throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        byte [] buf = new byte[1024];
        int num =-1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((num=gzip.read(buf,0,buf.length))!=-1){
            bos.write(buf,0,num);
        }
        gzip.close();
        bis.close();
        byte[] ret = bos.toByteArray();
        bos.flush();
        bos.close();
        return  ret;
    }

    public static void main(String[] args) throws Exception {
        //读取文件
        String readPath = System.getProperty("user.dir")+File.separatorChar+ "FaceUp"+File.separatorChar+"sources"+File.separatorChar+"ccc.jpg";
        System.out.println(readPath);
        File file = new File(readPath);
        FileInputStream in = new FileInputStream(file);
        byte[] data =new byte[in.available()];
        in.read();
        in.close();
        System.out.println("原始文件大小："+data.length);

        byte [] ret1 = GzipUtils.gzip(data);
        System.out.println("压缩之后的大小："+ret1.length);
        byte [] ret2 = GzipUtils.ungzip(ret1);
        System.out.println("解压缩之后的大小："+ret2.length);

        //写出文件
        String writePath= System.getProperty("user.dir")+ File.separatorChar+ "FaceUp"+File.separatorChar+"receive"+File.separatorChar+"ccc.jpg";
        FileOutputStream fos = new FileOutputStream(writePath);
        fos.write(ret2);
        fos.close();

    }
}

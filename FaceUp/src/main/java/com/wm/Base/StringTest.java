package com.wm.Base;

/**
 * @Author:wangmiao
 * @Desc
 * String：适用于少量的字符串操作的情况
　　StringBuilder：适用于单线程下在字符缓冲区进行大量操作的情况
　　StringBuffer：适用多线程下在字符缓冲区进行大量操作的情况（线程安全的
首先说运行速度，或者说是执行速度，在这方面运行速度快慢为：StringBuilder > StringBuffer > String
 * @Date Created in   2018/2/26 .
 */
public class StringTest {
    public static final String s1;
    public static final String s2;
    static{
        s1="abc";
        s2="def";
    }
    public static void main(String[] args) {
        String s3=s1+s2;
        String s4="abcdef";
        System.out.println(s3==s4);
    }
}

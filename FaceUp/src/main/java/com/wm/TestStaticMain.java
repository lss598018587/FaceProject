package com.wm;

/**
 * Created by wangmiao on 2018/2/7.
 */
public class TestStaticMain {
    public static void main(String[] args) {
        //为了验证，类加载了，类里的子类中的static是否会加载

        TestStatic.test();// testStatic =TestStatic.min.testStatic;
    }
}

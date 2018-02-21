package com.wm;

/**
 * Created by wangmiao on 2018/2/7.
 */
public class TestStatic {
    public TestStatic(){
        System.out.println("init TestStatic");
    }
    public static void test(){
        System.out.println("qwe");
    }
    static class min{
        public static TestStatic testStatic = new TestStatic();
    }
}

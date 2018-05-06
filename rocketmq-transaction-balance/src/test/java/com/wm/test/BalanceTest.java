package com.wm.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangmiao on 2018/3/1.
 */
public class BalanceTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        context.start();
    }
}

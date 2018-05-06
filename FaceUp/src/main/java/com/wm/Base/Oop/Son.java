package com.wm.Base.Oop;

/**
 * Created by wangmiao on 2018/3/14.
 */
public class Son extends Wile {
    public Son() {
        setName("wine");
    }

    @Override
    public void speak() {
        System.out.println("我再喝："+getName());
    }

    public void add(String a) {
        System.out.println("son add"+a);
        sum();
    }

    @Override
    public void sum() {
        System.out.println("son sum");
//        add();add?
    }
}

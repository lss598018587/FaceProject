package com.wm.Base.Oop;

/**
 * Created by wangmiao on 2018/3/14.
 */
public class Son2 extends Wile{
    public Son2() {
        setName("水");
    }

    @Override
    public void speak() {
        System.out.println("我在喝："+getName());
    }
}

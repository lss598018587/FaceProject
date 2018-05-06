package com.wm.Base.Oop;

/**
 * Created by wangmiao on 2018/3/14.
 */
public class Wile {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void speak(){
        System.out.println("要表达的："+getName());
    }

    public void add(){
        System.out.println("fu add");
        sum();
    }
    public void sum(){
        System.out.println("fu sum");
    }
}

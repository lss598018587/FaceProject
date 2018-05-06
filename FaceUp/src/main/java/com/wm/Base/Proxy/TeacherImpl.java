package com.wm.Base.Proxy;

import com.wm.Base.Proxy.Teacher;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class TeacherImpl implements Teacher {
    @Override
    public void add(){
        System.out.println("加工资");
    }
    @Override
    public void cut(){
        System.out.println("jian工资");
    }
}

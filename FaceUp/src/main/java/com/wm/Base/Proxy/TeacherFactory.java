package com.wm.Base.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class TeacherFactory implements InvocationHandler{

    private Teacher teacher;

    public TeacherFactory(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getName());
        System.out.println("进行反射");
         method.invoke(teacher,args);
        System.out.println("反射结束");
//        return teachers;
        return null;
    }
}

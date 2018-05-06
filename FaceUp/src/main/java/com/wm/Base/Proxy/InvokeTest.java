package com.wm.Base.Proxy;


import java.lang.reflect.Proxy;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class InvokeTest {
    public static void main(String[] args) {
        Teacher teacher = new TeacherImpl();
        TeacherFactory t = new TeacherFactory(teacher);
        Teacher t2 =(Teacher) Proxy.newProxyInstance(TeacherFactory.class
                        .getClassLoader(), teacher.getClass().getInterfaces(),t);
                //t.invoke(InvokeTest.class.getClassLoader(),Teacher.class.getInterfaces(),null);
        t2.add();
        t2.cut();
    }
}

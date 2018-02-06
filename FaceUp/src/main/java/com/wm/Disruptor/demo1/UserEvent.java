package com.wm.Disruptor.demo1;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/25 .
 */
public class UserEvent {
    private String userName;
    private int age;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}

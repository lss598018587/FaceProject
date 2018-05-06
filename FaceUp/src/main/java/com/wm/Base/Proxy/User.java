package com.wm.Base.Proxy;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wangmiao on 2018/3/13.
 */
public class User {
    private static User user;

//    StringBuffer sb  = new StringBuffer() ;
    StringBuilder sb = new StringBuilder();
    private User() {
    }

    public static User getInstance(){
        if(user==null){
            return TestUser.user1;
        }else{
            return user;
        }
    }
    public void add(){
            String c = sb.append("q").toString();
            System.out.println(c.length()+"---");
    }
    static class TestUser{
        static User user1 = new User();
    }


}

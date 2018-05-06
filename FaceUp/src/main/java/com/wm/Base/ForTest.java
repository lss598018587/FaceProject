package com.wm.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class ForTest {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        for(int s : list){
            if(s==5){
                list.remove(s);
            }
        }
        System.out.println("end ");
    }
}

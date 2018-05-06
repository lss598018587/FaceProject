package com.wm.Base;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class ArrayListTest {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(2);
        list.get(2);
        list.remove(1);
//        list.add(2);
//        list.add(2);
//        list.add(2);
//        list.add(2);
//        list.add(2);
//        list.add(2);
//        list.add(2);
//        list.add(2);
//        list.add(2);
        int arr [] = new int[]{1,2,3,4,5};
        System.out.println(Arrays.toString(arr));
        arr = Arrays.copyOf(arr,10);
        arr[6] =10;
        System.out.println(Arrays.toString(arr));

    }
}

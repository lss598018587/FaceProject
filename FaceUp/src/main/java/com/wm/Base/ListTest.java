package com.wm.Base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author:wangmiao
    1.ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。
    2.对于随机访问get和set，ArrayList觉得优于LinkedList，因为LinkedList要移动指针。
    3.对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。
    ArrayList内部是使用可増长数组实现的，所以是用get和set方法是花费常数时间的，但是如果插入元素和删除元素，除非插入和删除的位置都在表末尾，否则代码开销会很大，因为里面需要数组的移动。
    LinkedList是使用双链表实现的，所以get会非常消耗资源，除非位置离头部很近。但是插入和删除元素花费常数时间。

使用Collections.synchronizedList() 线程安全
List<Map<String,Object>> data=Collections.synchronizedList(new ArrayList<Map<String,Object>>());
 * @Desc
 * @Date Created in   2018/2/26 .
 */
public class ListTest {
    static final int N=150000;
    static long timeList(List list){
        long start=System.currentTimeMillis();
        Object o = new Object();
        for(int i=0;i<N;i++) {
            list.add(0, o);
        }
        return System.currentTimeMillis()-start;
    }
    static long readList(List list){
        long start=System.currentTimeMillis();
        for(int i=0,j=list.size();i<j;i++){

        }
        return System.currentTimeMillis()-start;
    }
    static long getList(List list){
        long start=System.currentTimeMillis();
        System.out.println(list.get(N-1));
        return System.currentTimeMillis()-start;
    }

    static List addList(List list){
        Object o = new Object();
        for(int i=0;i<N;i++) {
            list.add(0, o);
        }
        return list;
    }
    public static void main(String[] args) {
        List list1 = new ArrayList();
        List list2 = new LinkedList();
        System.out.println("ArrayList添加"+N+"条耗时："+timeList(list1));
        System.out.println("LinkedList添加"+N+"条耗时："+timeList(list2));

//        List list1=addList(new ArrayList<>());
//        List list2=addList(new LinkedList<>());
//        System.out.println("ArrayList查找"+N+"条耗时："+getList(list1));
//        System.out.println("LinkedList查找"+N+"条耗时："+getList(list2));

    }
}

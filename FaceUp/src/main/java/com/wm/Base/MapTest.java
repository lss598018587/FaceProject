package com.wm.Base;

import java.util.*;

/**
 * @Author:wangmiao
 * @Desc map的测试
 * HashTable 线程安全的
 * ConcurrentHashMap 线程安全的map  相当于多个HashTable
 * treemap 可以自定义排序规则
 * @Date Created in   2018/2/26 .
 */
public class MapTest {
    public static void main(String[] args) {
        //HashMap
//        System.out.println("------HashMap无序输出------");
//        HashMap<String,String> hsMap = new HashMap();
//        hsMap.put("3", "Value3");
//        hsMap.put("1", "Value1");
//        hsMap.put("2", "Value2");
//        hsMap.put("b", "ValueB");
//        hsMap.put("a", "ValueA");
////        Iterator it = hsMap.entrySet().iterator();
////        while (it.hasNext()) {
////            Map.Entry e = (Map.Entry) it.next();
////            System.out.println("Key: " + e.getKey() + "--Value: "
////                    + e.getValue());
////        }
////        for(Map.Entry<String,String> e : hsMap.entrySet()){
////            System.out.println("Key: " + e.getKey() + "--Value: "
////                    + e.getValue());
////        }
//        //LinkedHashMap
//        System.out.println("--LinkedHashMap根据输入的顺序输出--");
//        LinkedHashMap lhsMap = new LinkedHashMap();
//        lhsMap.put("3", "Value3");
//        lhsMap.put("1", "Value1");
//        lhsMap.put("2", "Value2");
//        lhsMap.put("b", "ValueB");
//        lhsMap.put("a", "ValueA");
//        Iterator lit = lhsMap.entrySet().iterator();
//        while (lit.hasNext()) {
//            Map.Entry e = (Map.Entry) lit.next();
//            System.out.println("Key: " + e.getKey() + "--Value: " + e.getValue());
//        }

        /**
         * 返回1的排在后面 o1 o2 返回1 输出是  o1 o2
         * 返回-1排在前面 o1 o2 返回 -1 输出是 o2 o1
         */
        TreeMap<String,String> treeMap = new TreeMap<String,String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o1.equals("其他")){
                    System.out.println(o1);
                    return  1;
                }
                if(o2.equals("其他")){
                    return  -1;
                }
                return o1.compareTo(o2);
            }
        });
        treeMap.put("OPPO","qwe");
        treeMap.put("其他","qwe2");
        treeMap.put("vivo","qwe2");
        treeMap.put("iPhone","qwe2");
        treeMap.put("三星","qwe2");
        treeMap.put("华为","qwe2");
        treeMap.put("小米","qwe2");
        treeMap.put("荣耀","qwe2");
        System.out.println(treeMap);


    }
}

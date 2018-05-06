package com.wm.Base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class LinkMapTest {
    public static void main(String[] args) {
        LinkedHashMap<String,Integer> map = new LinkedHashMap(6,0.75f,true);
        map.put("e",222);
        map.put("a",3);
        map.put("c",2);
        map.put("u",211);
        map.put("w",2);
        map.put("p",2);
//        System.out.println(map.get("u"));
        System.out.println(map.put("u",333));
//        HashMap<String,Integer> map1 = new HashMap<>();
//        map1.get("2");
        for(Map.Entry entry : map.entrySet()){
            System.out.println(entry.getKey());
        }
    }
}

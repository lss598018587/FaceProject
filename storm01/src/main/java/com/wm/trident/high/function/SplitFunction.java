package com.wm.trident.high.function;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class SplitFunction extends BaseFunction {

    static Map<String,Integer> map=new HashMap<String, Integer>();

    static {
        map.put("java",1);
        map.put("php",2);
        map.put("ruby",3);
        map.put("c++",4);
        map.put("C#",5);
        map.put("python",6);
    }

    public void execute(TridentTuple tuple, TridentCollector collector) {

        String sub = tuple.getString(0);
        System.out.println("sub>>>>"+sub);
        String arrs[] = sub.split(" ");
        for(String arr : arrs){
            Integer count = map.get(arr);
            collector.emit(new Values(arr,count));
        }
    }
}

package com.wm.trident.function;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class Result extends BaseFunction {
    public void execute(TridentTuple tuple, TridentCollector collector) {
        System.out.println("222222222传进来的内容为："+tuple);
        Integer a = tuple.getIntegerByField("a");
        Integer b = tuple.getIntegerByField("b");
        Integer c = tuple.getIntegerByField("c");
        Integer d = tuple.getIntegerByField("d");
        System.out.printf("a = %s , b = %s ,b = %s,b = %s",a,b,c,d);
        System.out.println();
        Integer sum = tuple.getIntegerByField("sum");
        System.out.println("sum = " + sum);
    }
}

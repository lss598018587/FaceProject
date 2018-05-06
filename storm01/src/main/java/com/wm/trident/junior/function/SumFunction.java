package com.wm.trident.junior.function;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class SumFunction extends BaseFunction {
    public void execute(TridentTuple tuple, TridentCollector collector) {
        System.out.println("传入的内容："+tuple);
        //获取a、b两个域
        int a = tuple.getInteger(0);
        int b = tuple.getInteger(1);

        int sum = a+b;
        //发射数据
        collector.emit(new Values(sum));
    }
}

package com.wm.trident.junior.function;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class CheckFilter extends BaseFilter {
    public boolean isKeep(TridentTuple tuple) {
        int a = tuple.getInteger(0);
        int b = tuple.getInteger(1);
        if((a+b)%2==0){
            return true;
        }
        return false;
    }
}

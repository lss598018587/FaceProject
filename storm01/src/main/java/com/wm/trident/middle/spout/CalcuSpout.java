package com.wm.trident.middle.spout;

import backtype.storm.tuple.Fields;
import storm.trident.testing.FixedBatchSpout;

import java.util.List;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class CalcuSpout extends FixedBatchSpout {
    /**
     *
     * @param fields 声明输入的域字段
     * @param maxBatchSize 设置批处理大小
     * @param outputs       设置数据内容
     */
    public CalcuSpout(Fields fields, int maxBatchSize, List<Object>... outputs) {
        super(fields, maxBatchSize, outputs);
    }
}

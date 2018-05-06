package com.wm.hello.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class PrintBolt  extends BaseBasicBolt{
    private static final Log log = LogFactory.getLog(PrintBolt.class);

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        //获取上个组建所声明的field
        String print = tuple.getStringByField("params1");
        String print2 = tuple.getStringByField("params2");
        log.info(Thread.currentThread().getName()+"【Print111111】:"+print);
        log.info(Thread.currentThread().getName()+"【Print222222】:"+print2);
        //传播给下个bolt
        collector.emit(new Values(print,print2));
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields("params1","params2"));
    }
}

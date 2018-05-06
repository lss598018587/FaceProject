package com.wm.middle.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * 消息重发
 * Created by wangmiao on 2018/3/10.
 */
public class SplitBolt implements IRichBolt {
    private static final Log log = LogFactory.getLog(WriteBolt.class);

    private OutputCollector collector;

    private boolean flag = false;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }

    public void execute(Tuple tuple) {
        try {

            //获取上个组建所声明的field
            String subjects = tuple.getStringByField("subjects");

//            if (!flag && subjects.equals("flume,activity")) {
//                flag = true;
//                int n =  1/0;
//            }

            String[] words = subjects.split(",");
            for (String word : words) {
                this.collector.emit(tuple, new Values(word));
            }
            collector.ack(tuple);
        }catch (Exception e){
            e.printStackTrace();
            collector.fail(tuple);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }


    public void cleanup() {

    }
}

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

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息重发
 * Created by wangmiao on 2018/3/10.
 */
public class WriteBolt implements IRichBolt{
    private static final Log log = LogFactory.getLog(WriteBolt.class);

    private OutputCollector collector;

    private FileWriter writer;

    private boolean flag =false;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector=collector;
        try {
            writer = new FileWriter("/Users/wangmiao/Downloads/tmp/message.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute(Tuple tuple) {
        System.out.println("tuple>> "+tuple);
        //获取上个组建所声明的field
        String word = tuple.getString(0);
        System.out.println("word>>"+word);
        try {
            if(!flag && word.equals("hadoop")){
                flag = true;
                int n= 1/0;
            }
            writer.write(word);
            writer.write("\r\n");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            collector.fail(tuple);
        }
        collector.emit(tuple,new Values(word));
        collector.ack(tuple);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }



    public void cleanup() {

    }
}

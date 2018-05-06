package com.wm.junior.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class WordSplitBolt implements IRichBolt {
    private static final Log log = LogFactory.getLog(WordCountBolt.class);

    private OutputCollector collector;


    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector=collector;
    }

    public void execute(Tuple tuple) {
        //获取上个组建所声明的field
        String sentences = tuple.getStringByField("sentences");
        String [] words = sentences.split(" ");
        for(String word : words){
            this.collector.emit(new Values(word));
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

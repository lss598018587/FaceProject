package com.wm.middle.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class MessageSpout implements IRichSpout  {

    private final Log log = LogFactory.getLog(MessageSpout.class);
    SpoutOutputCollector collector;

      static AtomicInteger index =new AtomicInteger(0);
      private  String subjects[] = new String[]{
                "groovy,oceanbase" ,
                "openfire,restful",
                "flume,activity",
                "hadoop,hbase",
                "spark,sqoop"
        };

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
    }

    public void close() {

    }

    public void activate() {

    }

    public void deactivate() {

    }

    /**
     * 一直轮询，罪恶的根源
     */
    public void nextTuple() {
        int limit_nums = index.get();
        if(limit_nums<subjects.length){
            String b =subjects[limit_nums];
            this.collector.emit(new Values(b),limit_nums);
            index.incrementAndGet();
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 声明发送数据的field
     * @param declarer
     */
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("subjects"));
    }
    public void ack(Object msgId) {
        System.out.println("【消息发送成功：】（msgId = "+msgId+"）");
    }

    public void fail(Object msgId) {
        System.out.println("【消息发送失败！！！】（msgId = "+msgId+"）");
        System.out.println("【消息发送中】");
        this.collector.emit(new Values(subjects[(Integer) msgId]),msgId);
        System.out.println("【发送成功！！！！！！！！！！】");
    }


    public Map<String, Object> getComponentConfiguration() {
        return null;
    }


}

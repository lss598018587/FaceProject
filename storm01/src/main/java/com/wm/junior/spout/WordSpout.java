package com.wm.junior.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class WordSpout extends BaseRichSpout{

    private final Log log = LogFactory.getLog(WordSpout.class);
    SpoutOutputCollector collector;

      static AtomicInteger Max_nums =new AtomicInteger(0);
      private  String sentences[] ={
                ", women’s day reminds people the importance of women " ,
                        "and men should respect them. While as the society develops",
                " some people complain that the meaning of this day has been mislead. " ,
                        "It has been always advocated that men and women should be equal. Indeed "
        };

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
    }

    /**
     * 一直轮询，罪恶的根源
     */
    public void nextTuple() {
        int limit_nums = Max_nums.get();
//        log.info(Thread.currentThread().getName()+":外面：limit_nums>>"+limit_nums);
        while (limit_nums<sentences.length){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<Object> m = Arrays.asList( (Object) sentences[limit_nums]);
            this.collector.emit(m);
            log.info(Thread.currentThread().getName()+"里面：limit_nums>>"+limit_nums);
            limit_nums = Max_nums.incrementAndGet();
        }
    }

    /**
     * 声明发送数据的field
     * @param declarer
     */
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sentences"));
    }


    @Override
    public void close() {
        log.info("听说结束了能调用一下，试试看");
    }
}

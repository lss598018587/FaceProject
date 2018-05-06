package com.wm.hello.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class WmSpout  extends BaseRichSpout{

    private final Log log = LogFactory.getLog(WmSpout.class);
    SpoutOutputCollector collector;

    final static List<Object> list = new ArrayList<Object>();
      static AtomicInteger Max_nums =new AtomicInteger(1);
    static {
        list.add("聪明的王某1");
        list.add("聪明的王喵2");
        list.add("聪明的王淼3");
        list.add("聪明的王苗4");
        list.add("聪明的王5");
    }

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
    }

    /**
     * 一直轮询，罪恶的根源
     */
    public void nextTuple() {
        int limit_nums = Max_nums.get();
//        log.info(Thread.currentThread().getName()+":外面：limit_nums>>"+limit_nums);
        while (limit_nums<40){
            final Random r =new Random();
            int num = r.nextInt(5);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String msg = Thread.currentThread().getName()+"里面：limit_nums>>"+limit_nums;
            this.collector.emit(new Values(list.get(num),msg));
            log.info(msg);
            limit_nums = Max_nums.incrementAndGet();
        }
    }

    /**
     * 声明发送数据的field
     * @param declarer
     */
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields("params1","params2"));
    }
}

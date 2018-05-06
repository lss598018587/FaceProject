package com.wm.hello.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class WriteBolt extends BaseBasicBolt {
    private static final Log log = LogFactory.getLog(PrintBolt.class);

    int jishuqi =0;
    private FileWriter writer;

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        jishuqi++;
        String print = tuple.getStringByField("params1");
        String print2 = tuple.getStringByField("params2");
        try {
            if (writer == null) {
                if (System.getProperty("os.name").equals("Mac OS X")) {
                    writer = new FileWriter("/Users/wangmiao/Downloads/tmp/" + this + ".txt");
                } else {
                    writer = new FileWriter("/home/tmp/" + this + ".txt");
                }
            }
            log.info(Thread.currentThread().getName()+"【write】:写出文件");
            writer.write(Thread.currentThread().getName()+print+"========="+print2+"，计数："+jishuqi);
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    public static void main(String[] args) {
//        System.out.println(System.getProperty("os.name"));

    }


}
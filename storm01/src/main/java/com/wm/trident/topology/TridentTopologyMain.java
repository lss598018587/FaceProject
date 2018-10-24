package com.wm.trident.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.wm.trident.spout.CalcuSpout;
import storm.trident.Stream;
import storm.trident.testing.FixedBatchSpout;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class TridentTopology {

    public static void main(String[] args) throws InterruptedException {
        TridentTopology topology = new TridentTopology();
        FixedBatchSpout spout = new CalcuSpout(new Fields("a","b","c","d"),4,
                new Values(2,3,4,6),new Values(5,3,1,2),new Values(2,4,7,2));
        //是否循环
        spout.setCycle(false);
        Stream inputStream = topology.


        Config conf = new Config();
        conf.setNumWorkers(2);
        conf.setDebug(true);
        //设置最大batch处理数
        conf.setMaxSpoutPending(20);
        if(args.length==0){
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("wangmiao",conf,);
            TimeUnit.SECONDS.sleep(10);
            cluster.shutdown();
        }else {
            StormSubmitter.submitTopology("wangmiao",conf,);
        }
    }
}

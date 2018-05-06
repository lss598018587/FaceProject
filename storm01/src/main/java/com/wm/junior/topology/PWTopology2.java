package com.wm.junior.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.wm.junior.bolt.WordCountBolt;
import com.wm.junior.bolt.WordReportBolt;
import com.wm.junior.bolt.WordSplitBolt;
import com.wm.junior.spout.WordSpout;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class PWTopology2{
    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
        Config cfg = new Config();
        cfg.setNumWorkers(2);
        cfg.setDebug(true);
        TopologyBuilder builder =new TopologyBuilder();
        builder.setSpout("wm1",new WordSpout());
        builder.setBolt("wm2",new WordSplitBolt(),4).shuffleGrouping("wm1").setNumTasks(4);
        builder.setBolt("wm3",new WordCountBolt(),4).fieldsGrouping("wm2",new Fields("word")).setNumTasks(4);
        builder.setBolt("wm4",new WordReportBolt()).globalGrouping("wm3");


        //本地模式
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("wangmiao",cfg,builder.createTopology());
        TimeUnit.SECONDS.sleep(18);
        cluster.killTopology("wangmiao");
        cluster.shutdown();

        //集群模式
//        StormSubmitter.submitTopology("wangmiao",cfg,builder.createTopology());

    }
}

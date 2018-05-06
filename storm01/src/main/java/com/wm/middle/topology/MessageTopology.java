package com.wm.middle.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.wm.middle.bolt.WriteBolt;
import com.wm.middle.bolt.SplitBolt;
import com.wm.middle.spout.MessageSpout;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class MessageTopology {
    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
        Config cfg = new Config();
        cfg.setNumWorkers(2);
        cfg.setDebug(true);
        TopologyBuilder builder =new TopologyBuilder();
        builder.setSpout("wm1",new MessageSpout());
        builder.setBolt("wm2",new SplitBolt()).shuffleGrouping("wm1");
        builder.setBolt("wm3",new WriteBolt()).shuffleGrouping("wm2");


        //本地模式
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("wangmiao",cfg,builder.createTopology());
        TimeUnit.SECONDS.sleep(20);
        cluster.killTopology("wangmiao");
        cluster.shutdown();

        //集群模式
//        StormSubmitter.submitTopology("wangmiao",cfg,builder.createTopology());

    }
}

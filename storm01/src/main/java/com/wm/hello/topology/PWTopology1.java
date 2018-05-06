package com.wm.hello.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.wm.hello.bolt.PrintBolt;
import com.wm.hello.bolt.WriteBolt;
import com.wm.hello.spout.WmSpout;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class PWTopology1 {
    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
        Config cfg = new Config();
        cfg.setNumWorkers(2);
        cfg.setDebug(true);
        TopologyBuilder builder =new TopologyBuilder();
        //代表开启3个线程一直去跑WmSpout里的nextTuple方法
        builder.setSpout("wm1",new WmSpout(),3);
        //代表开启3个线程一直去跑PrintBolt里的execute方法，设置task，类似就是说我任务最多4个，线程超多这个任务数量不行
       //Thread《=task，线程最多开这么多
        builder.setBolt("wm2",new PrintBolt(),4).shuffleGrouping("wm1").setNumTasks(4);
        //不设置线程数，默认代表开启1个线程一直去跑WriteBolt里的execute方法
        //当线程数为3，tasks为4，开启3个线程去跑4个任务，所以会生成4个文件，有个线程要再跑一个task
        builder.setBolt("wm3",new WriteBolt(),3).shuffleGrouping("wm2").setNumTasks(4);

        //shuffleGrouping随机分组
//        builder.setBolt("wm3",new WriteBolt(),3).shuffleGrouping("wm2").setNumTasks(4);
        //fieldsGrouping设置字段分组
//        builder.setBolt("wm3",new WriteBolt(),3).fieldsGrouping("wm2",new Fields("params2")).setNumTasks(4);
        //allGrouping设置广播分组
//        builder.setBolt("wm3",new WriteBolt(),3).allGrouping("wm2").setNumTasks(4);
        //globalGrouping设置全局分，指定分到哪个租
//        builder.setBolt("wm3",new WriteBolt(),3).globalGrouping("wm2").setNumTasks(4);


        //本地模式
//        LocalCluster cluster = new LocalCluster();
//        cluster.submitTopology("wangmiao",cfg,builder.createTopology());
//        TimeUnit.SECONDS.sleep(18);
//        cluster.killTopology("wangmiao");
//        cluster.shutdown();

        //集群模式
        StormSubmitter.submitTopology("wangmiao",cfg,builder.createTopology());

    }
}

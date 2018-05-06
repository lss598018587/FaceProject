package com.wm.trident.middle.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.wm.trident.middle.function.WriteFunction;
import com.wm.trident.middle.spout.CalcuSpout;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.testing.FixedBatchSpout;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class StrategyTopology {

    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
        TridentTopology topology = new TridentTopology();
        FixedBatchSpout spout = new CalcuSpout(new Fields("sub"),4,
                new Values("java"),new Values("ruby"),new Values("python"),new Values("c++"),
                new Values("C#"));
        //是否循环
        spout.setCycle(true);
        //指定输入源spout
        Stream inputStream = topology.newStream("spout",spout);


        inputStream
                //按字段分组，和fieldsGrouping类似
//                .partitionBy(new Fields("sub"))
                //随机分组，和shuffleGrouping类似
//                .shuffle()
                //全局分组，就是全部线程集合到一个节点上，和globalGrouping类似
//                .global()
                //广播分组,和allGrouping类似
                .broadcast()
                .each(new Fields("sub"),new WriteFunction(),new Fields() ).parallelismHint(4);


        Config conf = new Config();
        conf.setNumWorkers(2);
        conf.setDebug(true);
        //设置最大batch处理数
        conf.setMaxSpoutPending(20);
        if(args.length==0){
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("wangmiao",conf,topology.build());
            TimeUnit.SECONDS.sleep(5);
            cluster.shutdown();
        }else {
            StormSubmitter.submitTopology(args[0],conf,topology.build());
        }
    }
}

package com.wm.trident.high.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.wm.trident.high.function.ResultFunction;
import com.wm.trident.high.function.SplitFunction;
import com.wm.trident.high.spout.CalcuSpout;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.FilterNull;
import storm.trident.operation.builtin.Sum;
import storm.trident.testing.FixedBatchSpout;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class WordCountTopology {

    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
        TridentTopology topology = new TridentTopology();
        FixedBatchSpout spout = new CalcuSpout(new Fields("subject"),4,
                new Values("java java php ruby c++"),
                new Values("java ruby ruby C# c++"),
                new Values("python php php php c++"),
                new Values("c++ c++ java java C#"));
        //是否循环
        spout.setCycle(false);
        //指定输入源spout
        Stream inputStream = topology.newStream("spout",spout);


        inputStream
                //按字段分组，和fieldsGrouping类似
//                .partitionBy(new Fields("sub"))
                //随机分组，和shuffleGrouping类似
                .shuffle()
                .each(new Fields("subject"),new SplitFunction(),new Fields("sub","subCount"))
                .groupBy(new Fields("sub"))
                .aggregate(  new Count(),new Fields("count"))
                .each(new Fields("sub", "count"),new ResultFunction(),new Fields() ).parallelismHint(4);


        Config conf = new Config();
        conf.setNumWorkers(2);
        conf.setDebug(true);
        //设置最大batch处理数
        conf.setMaxSpoutPending(20);
        if(args.length==0){
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("wangmiao",conf,topology.build());
            TimeUnit.SECONDS.sleep(10);
            cluster.shutdown();
        }else {
            StormSubmitter.submitTopology(args[0],conf,topology.build());
        }
    }
}

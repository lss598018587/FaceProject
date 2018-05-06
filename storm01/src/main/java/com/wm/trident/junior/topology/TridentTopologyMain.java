package com.wm.trident.junior.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.wm.trident.junior.function.CheckFilter;
import com.wm.trident.junior.function.Result;
import com.wm.trident.junior.function.ResultFilter;
import com.wm.trident.junior.function.SumFunction;
import com.wm.trident.junior.spout.CalcuSpout;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.testing.FixedBatchSpout;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class TridentTopologyMain {

    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
        TridentTopology topology = new TridentTopology();
        FixedBatchSpout spout = new CalcuSpout(new Fields("a","b","c","d"),4,
                new Values(2,3,4,6),new Values(5,3,1,2),new Values(2,4,7,2));
        //是否循环
        spout.setCycle(false);
        //指定输入源spout
        Stream inputStream = topology.newStream("spout",spout);

        /**
         * 要实现流spout - bolt 的模式，在trident里是使用each来做的
         * each方法参数：
         * 1。输入数据源参数名称："a","b","c","d"
         * 2.需要流转执行的function对象（也就是bolt对象）：new SumFunction（）
         * 3。指定function对象里的输出参数名称：sum
         */
//        inputStream.each(new Fields("a","b","c","d"),new SumFunction(),new Fields("sum"))
//                .each(new Fields("a","b","c","d","sum"),new Result(),new Fields());

        //过滤器的示例
        inputStream.each(new Fields("a","b","c","d"),new CheckFilter() )
                .each(new Fields("a","b","c","d" ),new ResultFilter(),new Fields());


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

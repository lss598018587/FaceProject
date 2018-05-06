package com.wm.drpc;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.StormSubmitter;
import backtype.storm.drpc.LinearDRPCInputDeclarer;
import backtype.storm.drpc.LinearDRPCTopologyBuilder;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.grouping.CustomStreamGrouping;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class BasicDRPCTopology {
    static class Miaomiao extends BaseBasicBolt{

        public void execute(Tuple tuple, BasicOutputCollector collector) {
            String input2 = tuple.getString(1);
            System.out.println("tuple.getValue(0)>>>>>>>>"+tuple.getValue(0));
            collector.emit(new Values(tuple.getValue(0),input2+"!"));
        }

        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id","result"));
        }
    }

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
        //创建drpc实例
        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("wangmiao");
        //添加bolt
        builder.addBolt(new Miaomiao(),3);
        Config config = new Config();
        if(args == null || args.length == 0){
            LocalDRPC drpc = new LocalDRPC();
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("miaoye",config,builder.createLocalTopology(drpc));
            for(String word : new String[]{"hello","goodbye"}){
                System.out.println("Result for \"" + word + "\":"+drpc.execute("wangmiao",word));
            }
            cluster.shutdown();
            drpc.shutdown();
        }else {
            config.setNumWorkers(2);
            StormSubmitter.submitTopology(args[0],config,builder.createRemoteTopology());
        }
    }
}

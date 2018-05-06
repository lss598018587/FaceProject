package com.wm.trident.middle.function;

import backtype.storm.topology.base.BaseBasicBolt;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class WriteFunction extends BaseFunction {
    private FileWriter writer;

    public void execute(TridentTuple tuple, TridentCollector tridentCollector) {
        try {
            if (writer == null) {
                if (System.getProperty("os.name").equals("Mac OS X")) {
                    writer = new FileWriter("/Users/wangmiao/Downloads/tmp/" + this + ".txt");
                } else {
                    writer = new FileWriter("/home/tmp/" + this + ".txt");
                }
            }
            writer.write(tuple.getStringByField("sub"));
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

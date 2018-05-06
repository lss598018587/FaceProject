package com.wm.drpc;

import backtype.storm.generated.DRPCExecutionException;
import backtype.storm.utils.DRPCClient;
import org.apache.thrift7.TException;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class DrpcMM {
    public static void main(String[] args) throws TException, DRPCExecutionException {
        DRPCClient client = new DRPCClient("h1",3772);
        for(String word : new String[]{"wm1","wm2","wm3"}){
            System.out.println(client.execute("wangmiao",word));
        }
    }
}

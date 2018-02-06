package com.wm.Design.MasterWork;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by wangmiao on 2018/1/19.
 */
public class Worker implements Runnable{
    private ConcurrentLinkedQueue<Task> workQueue = new ConcurrentLinkedQueue<Task>();
    private ConcurrentHashMap<String,Object> resultMap = new ConcurrentHashMap<>();

    public void setWorkQueue(ConcurrentLinkedQueue<Task> workQueue) {
        this.workQueue = workQueue;
    }

    public void setResultMap(ConcurrentHashMap<String, Object> resultMap) {
        this.resultMap = resultMap;
    }

    @Override
    public void run() {
        while (true){
            Task input = this.workQueue.poll();
            if(input ==null) break;
            Object object = handle(input);
            this.resultMap.put(Integer.toString(input.getId()),object);
        }
    }
    public Object handle(Task input ){
        Object output =null;
        try {
            //处理任务的耗时，比如操作修改数据库
            Thread.sleep(500);
            output = input.getMoney();
        }catch (Exception e){

        }
        return output;
    }

}

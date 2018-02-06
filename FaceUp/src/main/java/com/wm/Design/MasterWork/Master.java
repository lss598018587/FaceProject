package com.wm.Design.MasterWork;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by wangmiao on 2018/1/19.
 */
public class Master {

    //1.有一个盛放任务的容器
    private ConcurrentLinkedQueue<Task> workQueue = new ConcurrentLinkedQueue<Task>();

    //2.需要有一个盛放work的集合
    private HashMap<String,Thread> works = new HashMap<>();

    //3.需要一个盛放每一个work任务的结果集合
    private ConcurrentHashMap<String,Object> resultMap = new ConcurrentHashMap<>();

    public Master(Worker work, int workCount){
        work.setResultMap(this.resultMap);
        work.setWorkQueue(this.workQueue);
        for(int i=0;i<workCount;i++){
            this.works.put(Integer.toString(i),new Thread(work));
        }
    }
    //需要一个提交任务的方法
    public void submit(Task task){
        this.workQueue.add(task);
    }
    //需要有一个执行方法，启动所有的worker方法去执行
    public void execuse(){
        for(Map.Entry<String,Thread> entry : works.entrySet()){
            entry.getValue().start();
        }
    }
    //判断是否运行结束
    public boolean isComplete(){
        for(Map.Entry<String,Thread> entry: works.entrySet()){
            if(entry.getValue().getState() !=Thread.State.TERMINATED ){//判断线程是否全部结束
                return false;
            }
        }
        return true;
    }
    //计算结果方法
    public int getResult(){
        int result =0;
        for(Map.Entry<String,Object> entry : resultMap.entrySet()){
            result  += (Integer) entry.getValue();
        }
        return result;
    }
}

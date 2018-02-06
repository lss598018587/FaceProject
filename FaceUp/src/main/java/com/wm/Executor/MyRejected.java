package com.wm.Executor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by wangmiao on 2018/1/20.
 */
public class MyRejected implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println("自定义处理");
        System.out.println("当前被拒绝的任务为："+r.toString());
    }
}

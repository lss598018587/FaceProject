package com.wm.Executor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by wangmiao on 2018/1/20.
 */
public class ScheduledJob {
    public static void main(String[] args) {
        Temp comand = new Temp();
        ScheduledExecutorService t = Executors.newScheduledThreadPool(1);
        // 1s后执行，执行之后每5s再执行一次
        ScheduledFuture<?> scheduledFuture = t.scheduleWithFixedDelay(comand,1,5,TimeUnit.SECONDS);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        System.out.println("开始:"+format.format(new Date()));
    }
}

class Temp extends Thread{
    @Override
    public void run() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        System.out.println("打印:"+format.format(new Date()));
    }

}
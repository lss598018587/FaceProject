package com.wm.Base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class ExecutorTest {
    public static void main(String[] args) {
//        ExecutorService service = Executors.
//        ThreadPoolExecutor executor = new ThreadPoolExecutor()
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleWithFixedDelay(new Scheduled(),5,1, TimeUnit.SECONDS);
    }

    static class Scheduled extends Thread{
        @Override
        public void run() {
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            System.out.println("打印一下"+format.format(new Date()));
        }
    }
}

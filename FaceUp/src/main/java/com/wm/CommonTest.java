package com.wm;

import com.wm.Design.MasterWork.Task;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by wangmiao on 2018/1/16.
 */
public class CommonTest {

    public static synchronized long add(long m){
        return ++m;
    }

    public static void main(String args[]) throws ExecutionException, InterruptedException {
        ExecutorService executor  = Executors.newFixedThreadPool(5);
        MyCallable myCallable = new  MyCallable("t1");
//        FutureTask<String> futureTask = new FutureTask<String>(myCallable);
        Future<String> task =executor.submit(myCallable);


        System.out.println(task.cancel(true));
        System.out.println(task.get());
        System.out.println("整个程序Over");
        executor.shutdown();
    }

    static class MyCallable implements  Callable<String>{
        private String userName;
        public MyCallable(String name) {
            this.userName = name;
        }

        @Override
        public String call() throws Exception {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            System.out.println("start Time >>> "+ format.format(new Date()));
            Thread.sleep(5000);
            System.out.println("end Time >>> "+ format.format(new Date()));
            return this.userName;
        }
    }
}

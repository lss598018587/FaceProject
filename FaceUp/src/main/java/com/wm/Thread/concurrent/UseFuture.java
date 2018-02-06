package com.wm.Thread.concurrent;

import java.util.concurrent.*;

/**
 * Created by wangmiao on 2018/1/22.
 */
public class UseFuture implements Callable<String> {
    private String par;

    public UseFuture(String par) {
        this.par = par;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(5000);
        String result = this.par+"处理完成";
        return result;
    }
    //
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String queryStr = "query";
        FutureTask<String> futureTask = new FutureTask<String>(new UseFuture(queryStr));

        ExecutorService service = Executors.newFixedThreadPool(1);

        Future f1 = service.submit(futureTask);

        System.out.println("请求完毕");

        try {
            System.out.println("处理实际业务逻辑。。。。");
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("呵呵");
        System.out.println(futureTask.get());
        System.out.println("哈哈哈");
        service.shutdown();
    }
}

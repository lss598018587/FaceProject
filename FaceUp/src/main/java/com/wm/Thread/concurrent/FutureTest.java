package com.wm.Thread.concurrent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/3/2 .
 */
public class FutureTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newCachedThreadPool();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        Callable callable = new WaiMai();
        FutureTask<KuaiCan> task = new FutureTask<KuaiCan>(callable);
        service.execute(task);
        System.out.println("开始上课2s:"+format.format(new Date()));
        TimeUnit.SECONDS.sleep(2);
        System.out.println("课间休息2s:"+format.format(new Date()));
        TimeUnit.SECONDS.sleep(2);
        System.out.println("上厕所1s:"+format.format(new Date()));
        TimeUnit.SECONDS.sleep(1);
        System.out.println("外卖打电话说要来了:"+format.format(new Date()));
        KuaiCan kc =  task.get();
        System.out.println("收到外卖："+kc.getName()+"  time:"+format.format(new Date()));
        System.out.println("eat....");

    }

    static class WaiMai implements Callable{

        @Override
        public KuaiCan call() throws Exception {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
            KuaiCan kc = new KuaiCan();
            kc.setName("大肉饭");
            System.out.println("快餐制作中。。。。。"+format.format(new Date()));
            TimeUnit.SECONDS.sleep(2);
            System.out.println("制作完毕，开始配送。。。。。"+format.format(new Date()));
            TimeUnit.SECONDS.sleep(9);
            return kc;
        }
    }

    static class KuaiCan{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
